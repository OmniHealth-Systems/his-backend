package com.clinic.empleados_service.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EspecialidadDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String codigo;
}