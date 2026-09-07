package com.clinic.Informe_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformeDTO {
    private Long idInforme;
    private Long idCita;
    private Long idPaciente;
    private Long idDoctor;
    private LocalDateTime fechaEmision;
    private String tipoInforme;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private String estado;
}