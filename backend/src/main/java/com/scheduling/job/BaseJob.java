package com.scheduling.job;

import com.scheduling.entity.ScheduleExecution;
import com.scheduling.repository.ScheduleExecutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;

@Slf4j
public abstract class BaseJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        long startTime = System.currentTimeMillis();
        String scheduleId = context.getJobDetail().getJobDataMap().getString("scheduleId");
        String scheduleName = context.getJobDetail().getJobDataMap().getString("scheduleName");
        
        ScheduleExecution execution = ScheduleExecution.builder()
            .scheduleId(scheduleId != null ? Long.parseLong(scheduleId) : null)
            .scheduleName(scheduleName)
            .executionTime(LocalDateTime.now())
            .build();
        
        try {
            log.info("Starting job execution for schedule: {} (ID: {})", scheduleName, scheduleId);
            
            executeJob(context);
            
            long duration = System.currentTimeMillis() - startTime;
            execution.setStatus("SUCCESS");
            execution.setDurationMs(duration);
            
            log.info("Job completed successfully for schedule: {} in {}ms", scheduleName, duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            execution.setStatus("FAILED");
            execution.setDurationMs(duration);
            execution.setErrorMessage(e.getMessage());
            
            log.error("Job failed for schedule: {}", scheduleName, e);
            throw new JobExecutionException(e);
            
        } finally {
            try {
                ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
                if (appContext != null) {
                    ScheduleExecutionRepository repository = appContext.getBean(ScheduleExecutionRepository.class);
                    repository.save(execution);
                }
            } catch (Exception e) {
                log.error("Failed to save execution history", e);
            }
        }
    }
    
    protected abstract void executeJob(JobExecutionContext context) throws Exception;
}
