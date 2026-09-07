package com.clinic.consultas_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamenSolicitadoEvent implements Serializable {
    private Long consultaId;
    private Long pacienteId;
    private Long doctorId;
    private List<Long> examenesIds;
    private String examenesNombres;
    private String indicacionesClinicas;
    private String fechaSolicitud;
}
