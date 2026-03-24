package com.scheduling.config;

import com.scheduling.listener.JobExecutionHistoryListener;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class QuartzConfig {
    
    @Autowired
    private Scheduler scheduler;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private JobExecutionHistoryListener jobExecutionHistoryListener;
    
    @PostConstruct
    public void init() {
        try {
            scheduler.getContext().put("applicationContext", applicationContext);
            scheduler.getListenerManager().addJobListener(jobExecutionHistoryListener);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Quartz context", e);
        }
    }
}
