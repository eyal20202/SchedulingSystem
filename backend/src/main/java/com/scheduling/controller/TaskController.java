package com.scheduling.controller;

import com.scheduling.dto.TaskDefinitionDTO;
import com.scheduling.dto.ParameterDefinitionDTO;
import com.scheduling.model.TaskDefinition;
import com.scheduling.service.TaskRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {
    
    private final TaskRegistry taskRegistry;
    
    public TaskController(TaskRegistry taskRegistry) {
        this.taskRegistry = taskRegistry;
    }
    
    @GetMapping
    public ResponseEntity<List<TaskDefinitionDTO>> getAllTasks() {
        log.info("Fetching all available tasks");
        Collection<TaskDefinition> tasks = taskRegistry.getAllTaskDefinitions();
        List<TaskDefinitionDTO> dtos = tasks.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDefinitionDTO> getTask(@PathVariable String taskId) {
        log.info("Fetching task: {}", taskId.replaceAll("[\r\n]", ""));
        return taskRegistry.getTaskDefinition(taskId)
            .map(this::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    private TaskDefinitionDTO toDTO(TaskDefinition task) {
        List<ParameterDefinitionDTO> params = task.getParameters().stream()
            .map(p -> ParameterDefinitionDTO.builder()
                .name(p.getName())
                .type(p.getType())
                .required(p.isRequired())
                .defaultValue(p.getDefaultValue())
                .description(p.getDescription())
                .build())
            .collect(Collectors.toList());
        
        return TaskDefinitionDTO.builder()
            .id(task.getId())
            .name(task.getName())
            .description(task.getDescription())
            .parameters(params)
            .build();
    }
}
