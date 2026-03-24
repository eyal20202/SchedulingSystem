package com.scheduling.service;

import com.scheduling.model.ParameterType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskRegistryTest {
    
    private final TaskRegistry taskRegistry = new TaskRegistry();
    
    @Test
    void testGetTaskDefinition_LogTask() {
        var taskDef = taskRegistry.getTaskDefinition("log_task");
        assertTrue(taskDef.isPresent());
        assertEquals("log_task", taskDef.get().getId());
        assertEquals("Log Task", taskDef.get().getName());
        assertTrue(taskDef.get().isReadOnly());
    }
    
    @Test
    void testGetTaskDefinition_EmailTask() {
        var taskDef = taskRegistry.getTaskDefinition("email_task");
        assertTrue(taskDef.isPresent());
        assertEquals("email_task", taskDef.get().getId());
        assertEquals("Dummy Email Task", taskDef.get().getName());
    }
    
    @Test
    void testGetTaskDefinition_NotFound() {
        var taskDef = taskRegistry.getTaskDefinition("unknown_task");
        assertFalse(taskDef.isPresent());
    }
    
    @Test
    void testGetAllTaskDefinitions() {
        var tasks = taskRegistry.getAllTaskDefinitions();
        assertEquals(2, tasks.size());
    }
    
    @Test
    void testIsValidTaskId() {
        assertTrue(taskRegistry.isValidTaskId("log_task"));
        assertTrue(taskRegistry.isValidTaskId("email_task"));
        assertFalse(taskRegistry.isValidTaskId("invalid_task"));
    }
    
    @Test
    void testLogTaskParameters() {
        var taskDef = taskRegistry.getTaskDefinition("log_task").orElseThrow();
        assertFalse(taskDef.getParameters().isEmpty());
        
        var messagParam = taskDef.getParameters().stream()
            .filter(p -> "message".equals(p.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(messagParam);
        assertEquals(ParameterType.STRING, messagParam.getType());
        assertFalse(messagParam.isRequired());
    }
    
    @Test
    void testEmailTaskParameters() {
        var taskDef = taskRegistry.getTaskDefinition("email_task").orElseThrow();
        assertFalse(taskDef.getParameters().isEmpty());
        
        var recipientParam = taskDef.getParameters().stream()
            .filter(p -> "recipientEmail".equals(p.getName()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(recipientParam);
        assertEquals(ParameterType.STRING, recipientParam.getType());
        assertTrue(recipientParam.isRequired());
    }
}
