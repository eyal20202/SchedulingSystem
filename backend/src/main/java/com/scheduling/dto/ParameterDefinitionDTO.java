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
public class ParameterDefinitionDTO {
    private String name;
    private ParameterType type;
    private boolean required;
    private String defaultValue;
    private String description;
}
