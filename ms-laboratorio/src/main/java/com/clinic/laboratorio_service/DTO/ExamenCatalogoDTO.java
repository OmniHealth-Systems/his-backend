package com.clinic.laboratorio_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamenCatalogoDTO {
    private Long id;

    @NotBlank(message = "El código del examen es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre del examen es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    private String descripcion;
    private Integer tiempoEntregaHoras;
    private String valoresReferenciaDefault;
    private Boolean activo;
}
