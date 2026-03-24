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
public class CreateScheduleDTO {
    private String name;
    private String description;
    private String taskId;
    private ScheduleFrequencyType frequencyType;
    private Integer frequencyValue;
    private String cronExpression;
    private String weekDays;
    private LocalDateTime oneTimeExecutionTime;
    private Boolean enabled;
    private List<ScheduleParameterDTO> parameters;
}
