package com.scheduling.repository;

import com.scheduling.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByEnabled(Boolean enabled);
    
    List<Schedule> findByStatus(String status);
    
    List<Schedule> findByTaskId(String taskId);
    
    @Query("SELECT s FROM Schedule s WHERE s.enabled = true AND s.status = 'ACTIVE'")
    List<Schedule> findActiveSchedules();
    
    @Query("SELECT s FROM Schedule s WHERE s.nextExecutionTime <= :now AND s.enabled = true AND s.status = 'ACTIVE' ORDER BY s.nextExecutionTime ASC")
    List<Schedule> findSchedulesDueForExecution(@Param("now") LocalDateTime now);
    
    Optional<Schedule> findByIdAndEnabled(Long id, Boolean enabled);
    
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.taskId = :taskId")
    long countByTaskId(@Param("taskId") String taskId);
}
