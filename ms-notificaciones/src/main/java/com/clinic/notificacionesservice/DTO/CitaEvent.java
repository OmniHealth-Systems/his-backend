package com.clinic.notificacionesservice.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaEvent {
    private Long id;
    private Long pacienteId;
    private Long doctorId;
    private LocalDateTime fechaHora;
    private String estado;
    private String tipoEvento; // CREATED, UPDATED, CANCELLED
}