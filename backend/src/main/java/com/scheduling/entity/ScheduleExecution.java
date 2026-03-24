package com.scheduling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_executions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;
    
    @Column(name = "schedule_name")
    private String scheduleName;
    
    @Column(name = "task_id")
    private String taskId;
    
    @Column(name = "execution_time", nullable = false)
    private LocalDateTime executionTime;
    
    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, FAILED
    
    @Column(name = "duration_ms")
    private Long durationMs;
    
    @Column(name = "error_message", length = 2000)
    private String errorMessage;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
