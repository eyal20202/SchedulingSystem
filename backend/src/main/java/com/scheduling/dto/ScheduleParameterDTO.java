package com.scheduling.dto;

import com.scheduling.model.ParameterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleParameterDTO {
    private Long id;
    private String parameterName;
    private String parameterValue;
    private ParameterType parameterType;
    private Boolean required;
}
