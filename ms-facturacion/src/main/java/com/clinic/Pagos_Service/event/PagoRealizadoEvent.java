package com.clinic.Pagos_Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoRealizadoEvent implements Serializable {
    private String pagoId;
    private String numeroComprobante;
    private String tipoComprobante; // BOLETA, FACTURA
    private Long pacienteId;
    private Long citaId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String fechaPago;
}
