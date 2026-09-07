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
public class InformeCompletoDTO {
    private Long idInforme;
    private LocalDateTime fechaEmision;
    private String tipoInforme;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private String estado;

    // Datos de otros microservicios
    private CitaDTO cita;
    private PacienteDTO paciente;
    private DoctorDTO doctor;
}