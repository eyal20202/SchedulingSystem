package com.scheduling.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public abstract class SimpleBaseJob implements Job {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        long startTime = System.currentTimeMillis();
        String scheduleId = context.getJobDetail().getJobDataMap().getString("scheduleId");
        String scheduleName = context.getJobDetail().getJobDataMap().getString("scheduleName");
        String timestamp = LocalDateTime.now().format(formatter);
        
        try {
            log.info("========================================");
            log.info("JOB EXECUTION START");
            log.info("Schedule ID: {}", scheduleId);
            log.info("Schedule Name: {}", scheduleName);
            log.info("Start Time: {}", timestamp);
            log.info("========================================");
            
            executeJob(context);
            
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("========================================");
            log.info("JOB EXECUTION SUCCESS");
            log.info("Schedule: {}", scheduleName);
            log.info("Duration: {}ms", duration);
            log.info("========================================");
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            log.error("========================================");
            log.error("JOB EXECUTION FAILED");
            log.error("Schedule: {}", scheduleName);
            log.error("Duration: {}ms", duration);
            log.error("Error: {}", e.getMessage());
            log.error("========================================", e);
            
            throw new JobExecutionException(e);
        }
    }
    
    protected abstract void executeJob(JobExecutionContext context) throws Exception;
}
