package com.scheduling.repository;

import com.scheduling.entity.ScheduleExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleExecutionRepository extends JpaRepository<ScheduleExecution, Long> {
    
    Page<ScheduleExecution> findByScheduleIdOrderByExecutionTimeDesc(Long scheduleId, Pageable pageable);
    
    List<ScheduleExecution> findTop10ByScheduleIdOrderByExecutionTimeDesc(Long scheduleId);
    
    Page<ScheduleExecution> findByStatusOrderByExecutionTimeDesc(String status, Pageable pageable);
}
