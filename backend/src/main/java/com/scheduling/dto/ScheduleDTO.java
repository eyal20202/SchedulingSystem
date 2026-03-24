package com.scheduling.dto;

import com.scheduling.model.ScheduleFrequencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleDTO {
    private Long id;
    private String name;
    private String description;
    private String taskId;
    private ScheduleFrequencyType frequencyType;
    private Integer frequencyValue;
    private String cronExpression;
    private String weekDays;
    private LocalDateTime oneTimeExecutionTime;
    private Boolean enabled;
    private LocalDateTime nextExecutionTime;
    private LocalDateTime lastExecutionTime;
    private String status;
    private List<ScheduleParameterDTO> parameters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
