package com.clinic.Pagos_Service.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
public class CitaDTO {
    private UUID id;
    private UUID pacienteId;
    private UUID doctorId;
    private UUID sedeId;
    private LocalDateTime fechaHora;
    private String estado;
    private String motivo;
}