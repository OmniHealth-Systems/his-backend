package com.clinic.empleados_service.DTO;

import com.clinic.empleados_service.domain.Especialidad;
import com.clinic.empleados_service.domain.Turno;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorDTO {
    private Long id;
    private String nombreCompleto;
    private String cmp;
    private Especialidad especialidad;
    private Turno turno;
}