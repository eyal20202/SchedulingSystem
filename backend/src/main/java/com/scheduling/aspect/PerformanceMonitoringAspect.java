package com.scheduling.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceMonitoringAspect {
    
    @Around("execution(* com.scheduling.service.*.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            if (duration > 1000) {
                log.warn("SLOW METHOD: {} took {}ms", methodName, duration);
            } else {
                log.debug("Method {} executed in {}ms", methodName, duration);
            }
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Method {} failed after {}ms: {}", methodName, duration, e.getMessage());
            throw e;
        }
    }
}
