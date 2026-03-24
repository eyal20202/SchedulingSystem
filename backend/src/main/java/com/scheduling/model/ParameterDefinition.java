package com.scheduling.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParameterDefinition {
    private String name;
    private ParameterType type;
    private boolean required;
    private String defaultValue;
    private String description;
}
