package com.clinic.notificacionesservice.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoEvent {
    private Long id;
    private Long pacienteId;
    private BigDecimal monto;
    private String estado; // CONFIRMADO, RECHAZADO, PENDIENTE
    private String tipo; // PAGO, REEMBOLSO
}