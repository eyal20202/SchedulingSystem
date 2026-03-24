package com.scheduling.listener;

import com.scheduling.entity.ScheduleExecution;
import com.scheduling.repository.ScheduleExecutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class JobExecutionHistoryListener implements JobListener {
    
    private final ScheduleExecutionRepository executionRepository;
    private final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();
    
    public JobExecutionHistoryListener(ScheduleExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }
    
    @Override
    public String getName() {
        return "JobExecutionHistoryListener";
    }
    
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        startTimeHolder.set(System.currentTimeMillis());
    }
    
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        startTimeHolder.remove();
    }
    
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        try {
            Long startTime = startTimeHolder.get();
            long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;
            
            String scheduleId = context.getJobDetail().getJobDataMap().getString("scheduleId");
            String scheduleName = context.getJobDetail().getJobDataMap().getString("scheduleName");
            String taskId = context.getJobDetail().getKey().getName().replace("schedule_", "");
            
            ScheduleExecution execution = ScheduleExecution.builder()
                .scheduleId(scheduleId != null ? Long.parseLong(scheduleId) : null)
                .scheduleName(scheduleName)
                .taskId(taskId)
                .executionTime(LocalDateTime.now())
                .status(jobException == null ? "SUCCESS" : "FAILED")
                .durationMs(duration)
                .errorMessage(jobException != null ? jobException.getMessage() : null)
                .build();
            
            executionRepository.save(execution);
            
            log.debug("Saved execution history for schedule: {}", scheduleName);
            
        } catch (Exception e) {
            log.error("Failed to save execution history", e);
        } finally {
            startTimeHolder.remove();
        }
    }
}
