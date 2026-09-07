package com.clinic.empleados_service.DTO;

import com.clinic.empleados_service.domain.Turno;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TurnoDTO {
    private Long id;
    private LocalDate fecha;
    private String lugar;
    private Turno.EstadoTurno estado;
}