package com.clinic.laboratorio_service.DTO;

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
public class OrdenLaboratorioRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    private Long doctorId;
    private Long consultaId;
    private String indicacionesMuestra;
    private String observacionesGenerales;

    @NotEmpty(message = "Debe solicitar al menos un examen de laboratorio")
    private List<Long> examenesIds;
}
