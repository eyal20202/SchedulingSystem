package com.scheduling.service;

import com.scheduling.model.ParameterDefinition;
import com.scheduling.model.ParameterType;
import com.scheduling.model.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class TaskRegistry {
    
    private final Map<String, TaskDefinition> taskDefinitions = new HashMap<>();
    
    public TaskRegistry() {
        initializePredefinedTasks();
    }
    
    private void initializePredefinedTasks() {
        // LogTask
        TaskDefinition logTask = TaskDefinition.builder()
            .id("log_task")
            .name("Log Task")
            .description("Writes a message to the application log")
            .taskClass("com.scheduling.job.LogTask")
            .readOnly(true)
            .parameters(Arrays.asList(
                ParameterDefinition.builder()
                    .name("message")
                    .type(ParameterType.STRING)
                    .required(false)
                    .defaultValue("Default log message")
                    .description("The message to log")
                    .build()
            ))
            .build();
        taskDefinitions.put("log_task", logTask);
        
        // DummyEmailTask
        TaskDefinition emailTask = TaskDefinition.builder()
            .id("email_task")
            .name("Dummy Email Task")
            .description("Simulates sending an email (dummy implementation)")
            .taskClass("com.scheduling.job.DummyEmailTask")
            .readOnly(true)
            .parameters(Arrays.asList(
                ParameterDefinition.builder()
                    .name("recipientEmail")
                    .type(ParameterType.STRING)
                    .required(true)
                    .description("The email recipient address")
                    .build(),
                ParameterDefinition.builder()
                    .name("subject")
                    .type(ParameterType.STRING)
                    .required(true)
                    .defaultValue("Scheduled Task Email")
                    .description("The email subject")
                    .build(),
                ParameterDefinition.builder()
                    .name("body")
                    .type(ParameterType.STRING)
                    .required(false)
                    .defaultValue("This is a scheduled email")
                    .description("The email body")
                    .build()
            ))
            .build();
        taskDefinitions.put("email_task", emailTask);
        
        log.info("Initialized {} predefined tasks", taskDefinitions.size());
    }
    
    public Optional<TaskDefinition> getTaskDefinition(String taskId) {
        return Optional.ofNullable(taskDefinitions.get(taskId));
    }
    
    public Collection<TaskDefinition> getAllTaskDefinitions() {
        return taskDefinitions.values();
    }
    
    public boolean isValidTaskId(String taskId) {
        return taskDefinitions.containsKey(taskId);
    }
    
    public Optional<TaskDefinition> getTaskDefinitionByClass(String className) {
        return taskDefinitions.values().stream()
            .filter(t -> t.getTaskClass().equals(className))
            .findFirst();
    }
}
