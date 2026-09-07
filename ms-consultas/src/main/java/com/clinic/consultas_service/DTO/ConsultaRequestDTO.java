package com.clinic.consultas_service.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaRequestDTO {

    private Long citaId;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long doctorId;

    private String motivoConsulta;
    private String anamnesis;
    private String examenFisico;

    private SignosVitalesDTO signosVitales;

    @NotEmpty(message = "Debe registrar al menos un diagnóstico")
    @Valid
    private List<DiagnosticoDTO> diagnosticos;

    @Valid
    private List<RecetaDTO> recetas;

    private String planTratamiento;

    private Boolean requiereExamenLaboratorio;
    private String examenesSolicitados;
}
