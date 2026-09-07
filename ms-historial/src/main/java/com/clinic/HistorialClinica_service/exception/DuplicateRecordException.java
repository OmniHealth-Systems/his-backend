package com.clinic.HistorialClinica_service.exception;
public class DuplicateRecordException extends RuntimeException {
    public DuplicateRecordException(String message) {
        super(message);
    }
}