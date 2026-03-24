package com.scheduling.advice;

import com.scheduling.dto.ErrorResponseDTO;
import com.scheduling.exception.InvalidParameterException;
import com.scheduling.exception.ScheduleNotFoundException;
import com.scheduling.exception.TaskNotFoundException;
import com.scheduling.exception.SchedulerOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleScheduleNotFoundException(
            ScheduleNotFoundException ex, WebRequest request) {
        log.error("Schedule not found: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTaskNotFoundException(
            TaskNotFoundException ex, WebRequest request) {
        log.error("Task not found: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidParameterException(
            InvalidParameterException ex, WebRequest request) {
        log.error("Invalid parameter: {}", ex.getMessage());
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(SchedulerOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleSchedulerOperationException(
            SchedulerOperationException ex, WebRequest request) {
        log.error("Scheduler operation failed: {}", ex.getMessage(), ex);
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Scheduler operation failed: " + ex.getMessage(),
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        String message = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed: " + message,
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error", ex);
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred: " + ex.getMessage(),
            System.currentTimeMillis(),
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
