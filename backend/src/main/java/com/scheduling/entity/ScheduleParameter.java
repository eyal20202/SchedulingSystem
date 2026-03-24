package com.scheduling.entity;

import com.scheduling.model.ParameterType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_parameters", indexes = {
    @Index(name = "idx_schedule_param_name", columnList = "parameter_name")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleParameter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Schedule schedule;
    
    @Column(nullable = false, length = 100)
    private String parameterName;
    
    @Column(length = 2000)
    private String parameterValue;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParameterType parameterType;
    
    @Column(nullable = false)
    private Boolean required;
}
