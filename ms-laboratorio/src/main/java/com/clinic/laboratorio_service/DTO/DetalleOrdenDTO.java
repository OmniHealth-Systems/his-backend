package com.clinic.laboratorio_service.DTO;

import com.clinic.laboratorio_service.domain.DetalleOrden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenDTO {
    private Long id;
    private Long examenId;
    private String examenCodigo;
    private String examenNombre;
    private Double examenPrecio;
    private String resultado;
    private String valoresReferencia;
    private String observaciones;
    private LocalDateTime fechaResultado;
    private DetalleOrden.EstadoDetalle estado;
}
