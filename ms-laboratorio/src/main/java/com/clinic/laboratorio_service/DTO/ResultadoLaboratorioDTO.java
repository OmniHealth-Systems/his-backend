package com.clinic.laboratorio_service.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoLaboratorioDTO {

    @NotBlank(message = "El resultado no puede estar vacío")
    private String resultado;

    private String valoresReferencia;
    private String observaciones;
}
