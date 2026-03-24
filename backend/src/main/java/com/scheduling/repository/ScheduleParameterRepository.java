package com.scheduling.repository;

import com.scheduling.entity.ScheduleParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleParameterRepository extends JpaRepository<ScheduleParameter, Long> {
    List<ScheduleParameter> findByParameterName(String parameterName);
    
    void deleteByScheduleId(Long scheduleId);
}
