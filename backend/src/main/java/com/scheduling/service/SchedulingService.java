package com.scheduling.service;

import com.scheduling.entity.Schedule;
import com.scheduling.entity.ScheduleParameter;
import com.scheduling.exception.InvalidParameterException;
import com.scheduling.exception.TaskNotFoundException;
import com.scheduling.model.ParameterDefinition;
import com.scheduling.model.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Locale;

@Slf4j
@Service
public class SchedulingService {

    private final TaskRegistry taskRegistry;
    private final Scheduler scheduler;

    public SchedulingService(TaskRegistry taskRegistry, Scheduler scheduler) {
        this.taskRegistry = taskRegistry;
        this.scheduler = scheduler;
    }

    public void scheduleJob(Schedule schedule) throws SchedulerException {
        validateSchedule(schedule);

        String jobName = "schedule_" + schedule.getId();
        String jobGroup = "scheduling_system";
        String triggerName = "trigger_" + schedule.getId();

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("scheduleId", String.valueOf(schedule.getId()));
        jobDataMap.put("scheduleName", schedule.getName());
        if (schedule.getParameters() != null) {
            schedule.getParameters().forEach(p ->
                jobDataMap.put("param_" + p.getParameterName(), p.getParameterValue() != null ? p.getParameterValue() : ""));
        }

        JobDetail jobDetail = JobBuilder.newJob()
            .ofType(getJobClass(schedule.getTaskId()))
            .withIdentity(jobName, jobGroup)
            .setJobData(jobDataMap)
            .storeDurably()
            .build();

        Trigger trigger = createTrigger(schedule, triggerName, jobGroup);

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Scheduled job: {} with trigger: {}", jobName, triggerName);
    }

    public void unscheduleJob(Long scheduleId) throws SchedulerException {
        String jobName = "schedule_" + scheduleId;
        String jobGroup = "scheduling_system";
        String triggerName = "trigger_" + scheduleId;

        if (scheduler.checkExists(new TriggerKey(triggerName, jobGroup))) {
            scheduler.unscheduleJob(new TriggerKey(triggerName, jobGroup));
        }

        if (scheduler.checkExists(new JobKey(jobName, jobGroup))) {
            scheduler.deleteJob(new JobKey(jobName, jobGroup));
        }

        log.info("Unscheduled job: {}", jobName);
    }

    public void rescheduleJob(Schedule schedule) throws SchedulerException {
        try {
            unscheduleJob(schedule.getId());
        } catch (SchedulerException e) {
            log.warn("Job may not exist, creating new one: {}", e.getMessage());
        }
        scheduleJob(schedule);
    }

    private Trigger createTrigger(Schedule schedule, String triggerName, String triggerGroup) {
        return switch (schedule.getFrequencyType()) {
            case ONE_TIME -> TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .startAt(java.util.Date.from(schedule.getNextExecutionTime()
                    .toInstant(ZoneOffset.UTC)))
                .build();

            case RECURRING_MINUTES -> TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(schedule.getFrequencyValue())
                    .repeatForever())
                .build();

            case RECURRING_HOURS -> TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInHours(schedule.getFrequencyValue())
                    .repeatForever())
                .build();

            case RECURRING_DAYS -> TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInHours(schedule.getFrequencyValue() * 24)
                    .repeatForever())
                .build();

            case WEEKLY -> TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(buildWeeklyCron(schedule.getWeekDays())))
                .build();

            case CRON_EXPRESSION -> TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCronExpression()))
                .build();
        };
    }

    private String buildWeeklyCron(String weekDays) {
        String[] days = weekDays.split(",");
        StringBuilder cron = new StringBuilder("0 0 0 ? * ");
        for (int i = 0; i < days.length; i++) {
            if (i > 0) cron.append(",");
            cron.append(dayToCronDayOfWeek(days[i].trim()));
        }
        return cron.toString();
    }

    private int dayToCronDayOfWeek(String day) {
        return switch (day.toUpperCase(Locale.ROOT)) {
            case "SUNDAY" -> 1;
            case "MONDAY" -> 2;
            case "TUESDAY" -> 3;
            case "WEDNESDAY" -> 4;
            case "THURSDAY" -> 5;
            case "FRIDAY" -> 6;
            case "SATURDAY" -> 7;
            default -> throw new IllegalArgumentException("Invalid day: " + day);
        };
    }

    private Class<? extends Job> getJobClass(String taskId) {
        return taskRegistry.getTaskDefinition(taskId)
            .map(this::loadJobClass)
            .orElseThrow(() -> new TaskNotFoundException("Task not found: " + taskId));
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Job> loadJobClass(TaskDefinition taskDef) {
        try {
            return (Class<? extends Job>) Class.forName(taskDef.getTaskClass());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load job class: " + taskDef.getTaskClass(), e);
        }
    }

    private void validateSchedule(Schedule schedule) {
        if (!taskRegistry.isValidTaskId(schedule.getTaskId())) {
            throw new TaskNotFoundException("Invalid task ID: " + schedule.getTaskId());
        }

        TaskDefinition taskDef = taskRegistry.getTaskDefinition(schedule.getTaskId()).orElseThrow();

        // Check all required parameters are provided
        for (ParameterDefinition paramDef : taskDef.getParameters()) {
            if (paramDef.isRequired()) {
                boolean found = schedule.getParameters() != null && schedule.getParameters().stream()
                    .anyMatch(p -> p.getParameterName().equals(paramDef.getName()) 
                        && p.getParameterValue() != null 
                        && !p.getParameterValue().trim().isEmpty());
                
                if (!found) {
                    throw new InvalidParameterException("Required parameter missing: " + paramDef.getName());
                }
            }
        }

        // Validate provided parameters exist in schema and validate types
        if (schedule.getParameters() != null) {
            for (ScheduleParameter param : schedule.getParameters()) {
                ParameterDefinition paramDef = taskDef.getParameters().stream()
                    .filter(p -> p.getName().equals(param.getParameterName()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidParameterException(
                        "Unknown parameter: " + param.getParameterName() + " for task: " + schedule.getTaskId()));
                
                // Validate parameter type
                validateParameterType(param, paramDef);
            }
        }
    }
    
    private void validateParameterType(ScheduleParameter param, ParameterDefinition paramDef) {
        if (param.getParameterValue() == null || param.getParameterValue().isEmpty()) {
            return; // Skip validation for empty optional parameters
        }
        
        String value = param.getParameterValue();
        
        try {
            switch (paramDef.getType()) {
                case NUMBER -> {
                    try {
                        Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        throw new InvalidParameterException(
                            "Parameter '" + param.getParameterName() + "' must be a valid number, got: " + value);
                    }
                }
                case BOOLEAN -> {
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                        throw new InvalidParameterException(
                            "Parameter '" + param.getParameterName() + "' must be 'true' or 'false', got: " + value);
                    }
                }
                case DATE -> {
                    try {
                        java.time.LocalDateTime.parse(value);
                    } catch (Exception e) {
                        throw new InvalidParameterException(
                            "Parameter '" + param.getParameterName() + "' must be a valid date-time (ISO format), got: " + value);
                    }
                }
                case STRING -> {
                    // String is always valid, but check max length
                    if (value.length() > 1000) {
                        throw new InvalidParameterException(
                            "Parameter '" + param.getParameterName() + "' exceeds maximum length of 1000 characters");
                    }
                }
            }
        } catch (InvalidParameterException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidParameterException(
                "Invalid value for parameter '" + param.getParameterName() + "': " + e.getMessage());
        }
    }
}
