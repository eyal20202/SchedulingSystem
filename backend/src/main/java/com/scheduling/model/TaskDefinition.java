package com.scheduling.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDefinition {
    private String id;
    private String name;
    private String description;
    private String taskClass;
    private List<ParameterDefinition> parameters;
    private boolean readOnly;
}
