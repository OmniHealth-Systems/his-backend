package com.clinic.consultas_service.DTO;

import com.clinic.consultas_service.domain.Diagnostico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticoDTO {
    private Long id;

    @NotBlank(message = "El código CIE-10 es obligatorio")
    private String codigoCie10;

    @NotBlank(message = "La descripción del diagnóstico es obligatoria")
    private String descripcion;

    @NotNull(message = "El tipo de diagnóstico es obligatorio")
    private Diagnostico.TipoDiagnostico tipo;
}
