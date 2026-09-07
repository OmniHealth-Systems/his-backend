package com.clinic.consultas_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetaEmitidaEvent implements Serializable {
    private Long consultaId;
    private Long pacienteId;
    private Long doctorId;
    private String medicamento;
    private String dosis;
    private String frecuencia;
    private String duracion;
    private Integer cantidadDeducir; // Unidades a deducir de stock en farmacia
    private String indicaciones;
    private String fechaEmision;
}
