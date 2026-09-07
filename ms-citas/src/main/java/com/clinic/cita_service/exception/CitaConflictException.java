package com.clinic.cita_service.exception;

public class CitaConflictException extends RuntimeException {
    public CitaConflictException(String message) {
        super(message);
    }

    public CitaConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}