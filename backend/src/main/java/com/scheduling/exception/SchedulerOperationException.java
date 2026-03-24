package com.scheduling.exception;

public class SchedulerOperationException extends RuntimeException {
    public SchedulerOperationException(String message) {
        super(message);
    }

    public SchedulerOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
