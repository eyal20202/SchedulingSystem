package com.scheduling.controller;

import com.scheduling.dto.CreateScheduleDTO;
import com.scheduling.dto.ScheduleDTO;
import com.scheduling.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Schedules", description = "Schedule management APIs")
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new schedule", description = "Creates a new schedule with the specified configuration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Schedule created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody CreateScheduleDTO createDto) {
        log.info("Creating new schedule: {}", createDto.getName().replaceAll("[\r\n]", ""));
        log.debug("Schedule details: taskId={}, frequencyType={}, enabled={}", 
            createDto.getTaskId(), createDto.getFrequencyType(), createDto.getEnabled());
        ScheduleDTO created = scheduleService.createSchedule(createDto, "ADMIN");
        log.info("Schedule created successfully with id: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get schedule by ID", description = "Retrieves a schedule by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schedule found"),
        @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    public ResponseEntity<ScheduleDTO> getSchedule(@PathVariable Long id) {
        log.info("Fetching schedule: {}", id);
        ScheduleDTO schedule = scheduleService.getSchedule(id);
        return ResponseEntity.ok(schedule);
    }
    
    @GetMapping
    public ResponseEntity<Page<ScheduleDTO>> getAllSchedules(Pageable pageable) {
        log.info("Fetching all schedules with pagination");
        Page<ScheduleDTO> schedules = scheduleService.getAllSchedules(pageable);
        return ResponseEntity.ok(schedules);
    }
    
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByTask(@PathVariable String taskId) {
        log.info("Fetching schedules for task: {}", taskId.replaceAll("[\r\n]", ""));
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByTask(taskId);
        return ResponseEntity.ok(schedules);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody CreateScheduleDTO updateDto) {
        log.info("Updating schedule: {}", id);
        ScheduleDTO updated = scheduleService.updateSchedule(id, updateDto, "ADMIN");
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        log.info("Deleting schedule: {}", id);
        try {
            scheduleService.deleteSchedule(id);
            log.info("Schedule deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting schedule: {}", id, e);
            throw e;
        }
    }
    
    @PostMapping("/{id}/toggle")
    public ResponseEntity<ScheduleDTO> toggleScheduleStatus(@PathVariable Long id) {
        log.info("Toggling schedule status: {}", id);
        ScheduleDTO updated = scheduleService.toggleScheduleStatus(id, "ADMIN");
        return ResponseEntity.ok(updated);
    }
}
