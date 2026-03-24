package com.scheduling.service;

import com.scheduling.dto.CreateScheduleDTO;
import com.scheduling.dto.ScheduleDTO;
import com.scheduling.entity.Schedule;
import com.scheduling.entity.ScheduleParameter;
import com.scheduling.exception.ScheduleNotFoundException;
import com.scheduling.exception.SchedulerOperationException;
import com.scheduling.repository.ScheduleRepository;
import com.scheduling.repository.ScheduleParameterRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final ScheduleParameterRepository parameterRepository;
    private final SchedulingService schedulingService;
    private final ScheduleMapper scheduleMapper;
    
    public ScheduleService(ScheduleRepository scheduleRepository, 
                          ScheduleParameterRepository parameterRepository,
                          SchedulingService schedulingService,
                          ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.parameterRepository = parameterRepository;
        this.schedulingService = schedulingService;
        this.scheduleMapper = scheduleMapper;
    }
    
    public ScheduleDTO createSchedule(CreateScheduleDTO createDto, String username) {
        log.info("Creating schedule: name={}, taskId={}, frequencyType={}", 
            createDto.getName(), createDto.getTaskId(), createDto.getFrequencyType());
        
        Schedule schedule = new Schedule();
        schedule.setName(createDto.getName());
        schedule.setDescription(createDto.getDescription());
        schedule.setTaskId(createDto.getTaskId());
        schedule.setFrequencyType(createDto.getFrequencyType());
        schedule.setFrequencyValue(createDto.getFrequencyValue());
        schedule.setCronExpression(createDto.getCronExpression());
        schedule.setWeekDays(createDto.getWeekDays());
        schedule.setNextExecutionTime(createDto.getOneTimeExecutionTime());
        schedule.setEnabled(createDto.getEnabled() != null ? createDto.getEnabled() : true);
        schedule.setStatus("ACTIVE");
        schedule.setCreatedBy(username != null ? username : "SYSTEM");
        schedule.setUpdatedBy(username != null ? username : "SYSTEM");
        schedule.setNextExecutionTime(calculateNextExecutionTime(schedule));
        
        // Map parameters
        if (createDto.getParameters() != null) {
            log.debug("Adding {} parameters to schedule", createDto.getParameters().size());
            List<ScheduleParameter> params = createDto.getParameters().stream()
                .map(paramDto -> {
                    ScheduleParameter param = new ScheduleParameter();
                    param.setSchedule(schedule);
                    param.setParameterName(paramDto.getParameterName());
                    param.setParameterValue(paramDto.getParameterValue());
                    param.setParameterType(paramDto.getParameterType());
                    param.setRequired(paramDto.getRequired());
                    return param;
                })
                .collect(Collectors.toList());
            schedule.setParameters(params);
        }
        
        log.debug("Saving schedule to database");
        Schedule saved = scheduleRepository.save(schedule);
        log.info("Schedule saved with id: {}", saved.getId());
        
        try {
            log.debug("Scheduling job with Quartz");
            schedulingService.scheduleJob(saved);
            log.info("Job scheduled successfully");
        } catch (SchedulerException e) {
            log.error("Failed to schedule job", e);
            throw new SchedulerOperationException("Failed to schedule job: " + e.getMessage(), e);
        }
        
        return scheduleMapper.toDTO(saved);
    }
    
    public ScheduleDTO updateSchedule(Long id, CreateScheduleDTO updateDto, String username) {
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));
        
        schedule.setName(updateDto.getName());
        schedule.setDescription(updateDto.getDescription());
        schedule.setTaskId(updateDto.getTaskId());
        schedule.setFrequencyType(updateDto.getFrequencyType());
        schedule.setFrequencyValue(updateDto.getFrequencyValue());
        schedule.setCronExpression(updateDto.getCronExpression());
        schedule.setWeekDays(updateDto.getWeekDays());
        schedule.setNextExecutionTime(updateDto.getOneTimeExecutionTime());
        schedule.setEnabled(updateDto.getEnabled());
        schedule.setUpdatedBy(username != null ? username : "SYSTEM");
        schedule.setNextExecutionTime(calculateNextExecutionTime(schedule));
        
        // Delete all old parameters by removing items from the collection one by one
        // This properly triggers Hibernate's cascade orphan removal
        List<ScheduleParameter> existingParams = new ArrayList<>(schedule.getParameters());
        for (ScheduleParameter param : existingParams) {
            schedule.getParameters().remove(param);
        }
        
        // Add new parameters
        if (updateDto.getParameters() != null) {
            for (com.scheduling.dto.ScheduleParameterDTO paramDto : updateDto.getParameters()) {
                ScheduleParameter param = new ScheduleParameter();
                param.setSchedule(schedule);
                param.setParameterName(paramDto.getParameterName());
                param.setParameterValue(paramDto.getParameterValue());
                param.setParameterType(paramDto.getParameterType());
                param.setRequired(paramDto.getRequired());
                schedule.getParameters().add(param);
            }
        }
        
        Schedule updated = scheduleRepository.save(schedule);
        
        try {
            schedulingService.rescheduleJob(updated);
        } catch (SchedulerException e) {
            log.error("Failed to reschedule job", e);
            throw new SchedulerOperationException("Failed to reschedule job: " + e.getMessage(), e);
        }
        
        return scheduleMapper.toDTO(updated);
    }
    
    public ScheduleDTO getSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));
        return scheduleMapper.toDTO(schedule);
    }
    
    public Page<ScheduleDTO> getAllSchedules(Pageable pageable) {
        Page<Schedule> schedules = scheduleRepository.findAll(pageable);
        return schedules.map(scheduleMapper::toDTO);
    }
    
    public List<ScheduleDTO> getSchedulesByTask(String taskId) {
        List<Schedule> schedules = scheduleRepository.findByTaskId(taskId);
        return schedules.stream().map(scheduleMapper::toDTO).collect(Collectors.toList());
    }
    
    public void deleteSchedule(Long id) {
        log.info("Attempting to delete schedule with id: {}", id);
        
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Schedule not found with id: {}", id);
                return new ScheduleNotFoundException("Schedule not found with id: " + id);
            });
        
        log.debug("Schedule found: {}", schedule.getName());
        
        try {
            log.debug("Unscheduling job from Quartz");
            schedulingService.unscheduleJob(id);
            log.info("Job unscheduled successfully");
        } catch (SchedulerException e) {
            log.error("Failed to unschedule job", e);
            throw new SchedulerOperationException("Failed to unschedule job: " + e.getMessage(), e);
        }
        
        log.debug("Deleting schedule from database");
        scheduleRepository.deleteById(id);
        log.info("Schedule deleted successfully with id: {}", id);
    }
    
    public ScheduleDTO toggleScheduleStatus(Long id, String username) {
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + id));
        
        schedule.setEnabled(!schedule.getEnabled());
        schedule.setUpdatedBy(username != null ? username : "SYSTEM");
        
        Schedule updated = scheduleRepository.save(schedule);
        
        try {
            if (schedule.getEnabled()) {
                schedulingService.scheduleJob(updated);
            } else {
                schedulingService.unscheduleJob(id);
            }
        } catch (SchedulerException e) {
            log.error("Failed to toggle schedule status", e);
            throw new SchedulerOperationException("Failed to toggle schedule status: " + e.getMessage(), e);
        }
        
        return scheduleMapper.toDTO(updated);
    }
    
    private LocalDateTime calculateNextExecutionTime(Schedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        
        return switch (schedule.getFrequencyType()) {
            case ONE_TIME -> schedule.getNextExecutionTime() != null
                ? schedule.getNextExecutionTime()
                : now.plusMinutes(1);
            case RECURRING_MINUTES -> now.plusMinutes(schedule.getFrequencyValue() != null ? schedule.getFrequencyValue() : 1);
            case RECURRING_HOURS -> now.plusHours(schedule.getFrequencyValue() != null ? schedule.getFrequencyValue() : 1);
            case RECURRING_DAYS -> now.plusDays(schedule.getFrequencyValue() != null ? schedule.getFrequencyValue() : 1);
            case WEEKLY -> now; // Quartz handles weekly scheduling
            case CRON_EXPRESSION -> now; // Quartz handles cron scheduling
        };
    }
}
