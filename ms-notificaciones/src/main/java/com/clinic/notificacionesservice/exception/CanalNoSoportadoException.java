package com.clinic.notificacionesservice.exception;

public class CanalNoSoportadoException extends RuntimeException {
    public CanalNoSoportadoException(String message) {
        super(message);
    }
}