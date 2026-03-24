package com.scheduling.controller;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    private final DataSource dataSource;
    private final Scheduler scheduler;
    
    public HealthController(DataSource dataSource, Scheduler scheduler) {
        this.dataSource = dataSource;
        this.scheduler = scheduler;
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        // Check database
        try (Connection conn = dataSource.getConnection()) {
            health.put("database", Map.of(
                "status", "UP",
                "type", "PostgreSQL"
            ));
        } catch (Exception e) {
            health.put("database", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
            health.put("status", "DOWN");
        }
        
        // Check Quartz scheduler
        try {
            boolean isStarted = scheduler.isStarted();
            int runningJobs = scheduler.getCurrentlyExecutingJobs().size();
            
            health.put("scheduler", Map.of(
                "status", isStarted ? "UP" : "DOWN",
                "started", isStarted,
                "runningJobs", runningJobs
            ));
            
            if (!isStarted) {
                health.put("status", "DOWN");
            }
        } catch (SchedulerException e) {
            health.put("scheduler", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
            health.put("status", "DOWN");
        }
        
        String status = (String) health.get("status");
        return "UP".equals(status) 
            ? ResponseEntity.ok(health) 
            : ResponseEntity.status(503).body(health);
    }
}
