package com.scheduling.service;

import com.scheduling.entity.Schedule;
import com.scheduling.entity.ScheduleParameter;
import com.scheduling.exception.ScheduleNotFoundException;
import com.scheduling.model.ParameterType;
import com.scheduling.model.ScheduleFrequencyType;
import com.scheduling.repository.ScheduleRepository;
import com.scheduling.repository.ScheduleParameterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {
    
    @Mock
    private ScheduleRepository scheduleRepository;
    
    @Mock
    private ScheduleParameterRepository parameterRepository;
    
    @Mock
    private SchedulingService schedulingService;
    
    @Mock
    private ScheduleMapper scheduleMapper;
    
    private ScheduleService scheduleService;
    
    private Schedule testSchedule;
    
    @BeforeEach
    void setUp() {
        scheduleService = new ScheduleService(scheduleRepository, parameterRepository, schedulingService, scheduleMapper);
        
        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setName("Test Schedule");
        testSchedule.setTaskId("log_task");
        testSchedule.setFrequencyType(ScheduleFrequencyType.ONE_TIME);
        testSchedule.setEnabled(true);
        testSchedule.setStatus("ACTIVE");
        testSchedule.setCreatedBy("TEST");
        testSchedule.setUpdatedBy("TEST");
        
        ScheduleParameter param = new ScheduleParameter();
        param.setParameterName("message");
        param.setParameterValue("Test message");
        param.setParameterType(ParameterType.STRING);
        param.setRequired(false);
        testSchedule.setParameters(Arrays.asList(param));
    }
    
    @Test
    void testGetSchedule_Success() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        
        assertDoesNotThrow(() -> scheduleService.getSchedule(1L));
        verify(scheduleRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetSchedule_NotFound() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.getSchedule(999L));
    }
    
    @Test
    void testToggleScheduleStatus() {
        testSchedule.setEnabled(true);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);
        
        assertDoesNotThrow(() -> scheduleService.toggleScheduleStatus(1L, "TEST"));
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }
    
    @Test
    void testDeleteSchedule() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        
        assertDoesNotThrow(() -> scheduleService.deleteSchedule(1L));
        verify(scheduleRepository, times(1)).deleteById(1L);
    }
}
