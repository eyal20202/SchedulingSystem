package com.scheduling.service;

import com.scheduling.dto.ScheduleDTO;
import com.scheduling.dto.ScheduleParameterDTO;
import com.scheduling.entity.Schedule;
import com.scheduling.entity.ScheduleParameter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ScheduleMapper {
    
    public ScheduleDTO toDTO(Schedule schedule) {
        return ScheduleDTO.builder()
            .id(schedule.getId())
            .name(schedule.getName())
            .description(schedule.getDescription())
            .taskId(schedule.getTaskId())
            .frequencyType(schedule.getFrequencyType())
            .frequencyValue(schedule.getFrequencyValue())
            .cronExpression(schedule.getCronExpression())
            .weekDays(schedule.getWeekDays())
            .oneTimeExecutionTime(schedule.getNextExecutionTime())
            .enabled(schedule.getEnabled())
            .nextExecutionTime(schedule.getNextExecutionTime())
            .lastExecutionTime(schedule.getLastExecutionTime())
            .status(schedule.getStatus())
            .parameters(schedule.getParameters().stream()
                .map(this::parameterToDTO)
                .collect(Collectors.toList()))
            .createdAt(schedule.getCreatedAt())
            .updatedAt(schedule.getUpdatedAt())
            .build();
    }
    
    private ScheduleParameterDTO parameterToDTO(ScheduleParameter param) {
        return ScheduleParameterDTO.builder()
            .id(param.getId())
            .parameterName(param.getParameterName())
            .parameterValue(param.getParameterValue())
            .parameterType(param.getParameterType())
            .required(param.getRequired())
            .build();
    }
}
