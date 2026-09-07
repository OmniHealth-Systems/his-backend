package com.clinic.HistorialClinica_service.exception;


public class HistorialNotFoundException extends RuntimeException {
    public HistorialNotFoundException(String message) {
        super(message);
    }
}