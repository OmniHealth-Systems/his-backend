package com.clinic.Informe_service.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaDTO {
    private Long idCita;
    private Long idPaciente;
    private Long idDoctor;
    private LocalDateTime fechaHora;
    private String estado;
    private String motivo;
    private String especialidad;
}
