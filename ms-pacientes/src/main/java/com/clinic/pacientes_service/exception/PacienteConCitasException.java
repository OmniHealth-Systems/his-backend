package com.clinic.pacientes_service.exception;

public class PacienteConCitasException extends RuntimeException {
    public PacienteConCitasException(String message) {
        super(message);
    }

    public PacienteConCitasException(String message, Throwable cause) {
        super(message, cause);
    }
}