package com.clinic.HistorialClinica_service.DTO;

import com.clinic.HistorialClinica_service.domain.RegistroMedico;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegistroMedicoDTO {
    private Long id;
    private RegistroMedico.TipoRegistro tipo;
    private LocalDateTime fechaRegistro;
    private Long doctorId;
    private Long citaId;

    private Long pacienteId;
    private String observaciones;
}