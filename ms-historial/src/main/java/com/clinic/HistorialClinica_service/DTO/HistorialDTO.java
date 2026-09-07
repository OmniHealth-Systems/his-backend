package com.clinic.HistorialClinica_service.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HistorialDTO {
    private Long id;
    private Long pacienteId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaActualizacion;
    private List<RegistroMedicoDTO> registros;
}

