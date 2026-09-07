package com.clinic.notificacionesservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CanalNoSoportadoException.class)
    public ResponseEntity<?> handleCanalNoSoportado(CanalNoSoportadoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CanalDeshabilitadoException.class)
    public ResponseEntity<?> handleCanalDeshabilitado(CanalDeshabilitadoException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }
}