package com.scheduling.entity;

import com.scheduling.model.ScheduleFrequencyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schedules", indexes = {
    @Index(name = "idx_schedule_task", columnList = "task_id"),
    @Index(name = "idx_schedule_status", columnList = "status"),
    @Index(name = "idx_schedule_created", columnList = "created_at")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Schedule name cannot be blank")
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @NotBlank(message = "Task ID cannot be blank")
    @Column(nullable = false, length = 100)
    private String taskId;
    
    @NotNull(message = "Frequency type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleFrequencyType frequencyType;
    
    private Integer frequencyValue; // For recurring schedules (e.g., every 5 minutes)
    
    private String cronExpression; // For CRON expressions
    
    private String weekDays; // For weekly: "MONDAY,WEDNESDAY,FRIDAY"
    
    @Column(nullable = false)
    private Boolean enabled;
    
    private LocalDateTime nextExecutionTime;
    
    private LocalDateTime lastExecutionTime;
    
    @Column(nullable = false, length = 50)
    private String status; // ACTIVE, PAUSED, COMPLETED, ERROR
    
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ScheduleParameter> parameters;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private String createdBy;
    
    @Column(nullable = false)
    private String updatedBy;
}
