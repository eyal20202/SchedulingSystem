package com.scheduling.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduling.dto.CreateScheduleDTO;
import com.scheduling.dto.ScheduleDTO;
import com.scheduling.model.ScheduleFrequencyType;
import com.scheduling.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    void testCreateSchedule() throws Exception {
        CreateScheduleDTO createDto = CreateScheduleDTO.builder()
            .name("Test Schedule")
            .description("Test Description")
            .taskId("log_task")
            .frequencyType(ScheduleFrequencyType.ONE_TIME)
            .enabled(true)
            .parameters(new ArrayList<>())
            .build();

        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
            .id(1L)
            .name("Test Schedule")
            .description("Test Description")
            .taskId("log_task")
            .frequencyType(ScheduleFrequencyType.ONE_TIME)
            .enabled(true)
            .status("ACTIVE")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .parameters(new ArrayList<>())
            .build();

        when(scheduleService.createSchedule(any(CreateScheduleDTO.class), any())).thenReturn(scheduleDTO);

        mockMvc.perform(post("/schedules")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Schedule"));
    }

    @Test
    void testGetSchedule() throws Exception {
        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
            .id(1L)
            .name("Test Schedule")
            .taskId("log_task")
            .frequencyType(ScheduleFrequencyType.ONE_TIME)
            .enabled(true)
            .status("ACTIVE")
            .parameters(new ArrayList<>())
            .build();

        when(scheduleService.getSchedule(anyLong())).thenReturn(scheduleDTO);

        mockMvc.perform(get("/schedules/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Schedule"));
    }

    @Test
    void testDeleteSchedule() throws Exception {
        doNothing().when(scheduleService).deleteSchedule(anyLong());

        mockMvc.perform(delete("/schedules/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testToggleSchedule() throws Exception {
        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
            .id(1L)
            .name("Test Schedule")
            .taskId("log_task")
            .frequencyType(ScheduleFrequencyType.ONE_TIME)
            .enabled(false)
            .status("ACTIVE")
            .parameters(new ArrayList<>())
            .build();

        when(scheduleService.toggleScheduleStatus(anyLong(), any())).thenReturn(scheduleDTO);

        mockMvc.perform(post("/schedules/1/toggle"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.enabled").value(false));
    }
}
