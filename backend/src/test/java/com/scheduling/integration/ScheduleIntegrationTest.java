package com.scheduling.integration;

import com.scheduling.dto.CreateScheduleDTO;
import com.scheduling.dto.ScheduleDTO;
import com.scheduling.model.ScheduleFrequencyType;
import com.scheduling.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ScheduleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    void testCreateAndRetrieveSchedule() {
        CreateScheduleDTO createDto = CreateScheduleDTO.builder()
            .name("Integration Test Schedule")
            .description("Test Description")
            .taskId("log_task")
            .frequencyType(ScheduleFrequencyType.RECURRING_MINUTES)
            .frequencyValue(5)
            .enabled(true)
            .parameters(new ArrayList<>())
            .build();

        ResponseEntity<ScheduleDTO> createResponse = restTemplate.postForEntity(
            "/api/schedules",
            createDto,
            ScheduleDTO.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getName()).isEqualTo("Integration Test Schedule");

        Long scheduleId = createResponse.getBody().getId();
        ResponseEntity<ScheduleDTO> getResponse = restTemplate.getForEntity(
            "/api/schedules/" + scheduleId,
            ScheduleDTO.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(scheduleId);

        restTemplate.delete("/api/schedules/" + scheduleId);
    }

    @Test
    void testToggleSchedule() {
        CreateScheduleDTO createDto = CreateScheduleDTO.builder()
            .name("Toggle Test Schedule")
            .taskId("log_task")
            .frequencyType(ScheduleFrequencyType.RECURRING_HOURS)
            .frequencyValue(1)
            .enabled(true)
            .parameters(new ArrayList<>())
            .build();

        ResponseEntity<ScheduleDTO> createResponse = restTemplate.postForEntity(
            "/api/schedules",
            createDto,
            ScheduleDTO.class
        );

        Long scheduleId = createResponse.getBody().getId();
        assertThat(createResponse.getBody().getEnabled()).isTrue();

        ResponseEntity<ScheduleDTO> toggleResponse = restTemplate.postForEntity(
            "/api/schedules/" + scheduleId + "/toggle",
            null,
            ScheduleDTO.class
        );

        assertThat(toggleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(toggleResponse.getBody().getEnabled()).isFalse();

        restTemplate.delete("/api/schedules/" + scheduleId);
    }
}
