package com.scheduling.controller;

import com.scheduling.entity.ScheduleExecution;
import com.scheduling.repository.ScheduleExecutionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/executions")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Execution History", description = "Job execution history APIs")
public class ExecutionHistoryController {
    
    private final ScheduleExecutionRepository executionRepository;
    
    public ExecutionHistoryController(ScheduleExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }
    
    @GetMapping
    @Operation(summary = "Get all executions", description = "Retrieves paginated list of all job executions")
    public ResponseEntity<Page<ScheduleExecution>> getAllExecutions(Pageable pageable) {
        Page<ScheduleExecution> executions = executionRepository.findAll(pageable);
        return ResponseEntity.ok(executions);
    }
    
    @GetMapping("/schedule/{scheduleId}")
    @Operation(summary = "Get executions by schedule", description = "Retrieves execution history for a specific schedule")
    public ResponseEntity<List<ScheduleExecution>> getExecutionsBySchedule(@PathVariable Long scheduleId) {
        List<ScheduleExecution> executions = executionRepository.findTop10ByScheduleIdOrderByExecutionTimeDesc(scheduleId);
        return ResponseEntity.ok(executions);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get executions by status", description = "Retrieves executions filtered by status (SUCCESS/FAILED)")
    public ResponseEntity<Page<ScheduleExecution>> getExecutionsByStatus(
            @PathVariable String status, 
            Pageable pageable) {
        Page<ScheduleExecution> executions = executionRepository.findByStatusOrderByExecutionTimeDesc(status, pageable);
        return ResponseEntity.ok(executions);
    }
}
