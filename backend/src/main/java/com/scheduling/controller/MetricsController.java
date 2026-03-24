package com.scheduling.controller;

import com.scheduling.repository.ScheduleExecutionRepository;
import com.scheduling.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    
    private final ScheduleRepository scheduleRepository;
    private final ScheduleExecutionRepository executionRepository;
    private final Scheduler scheduler;
    
    public MetricsController(ScheduleRepository scheduleRepository,
                            ScheduleExecutionRepository executionRepository,
                            Scheduler scheduler) {
        this.scheduleRepository = scheduleRepository;
        this.executionRepository = executionRepository;
        this.scheduler = scheduler;
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Schedule metrics
            long totalSchedules = scheduleRepository.count();
            long enabledSchedules = scheduleRepository.findByEnabled(true).size();
            long disabledSchedules = totalSchedules - enabledSchedules;
            
            metrics.put("schedules", Map.of(
                "total", totalSchedules,
                "enabled", enabledSchedules,
                "disabled", disabledSchedules
            ));
            
            // Execution metrics
            long totalExecutions = executionRepository.count();
            long successfulExecutions = executionRepository.findByStatusOrderByExecutionTimeDesc("SUCCESS", org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
            long failedExecutions = executionRepository.findByStatusOrderByExecutionTimeDesc("FAILED", org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
            
            metrics.put("executions", Map.of(
                "total", totalExecutions,
                "successful", successfulExecutions,
                "failed", failedExecutions,
                "successRate", totalExecutions > 0 ? (double) successfulExecutions / totalExecutions * 100 : 0
            ));
            
            // Quartz metrics
            int runningJobs = scheduler.getCurrentlyExecutingJobs().size();
            metrics.put("quartz", Map.of(
                "runningJobs", runningJobs,
                "schedulerName", scheduler.getSchedulerName(),
                "started", scheduler.isStarted()
            ));
            
            metrics.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(metrics);
            
        } catch (SchedulerException e) {
            log.error("Error getting metrics", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
