package com.clinic.Pagos_Service.service;

import com.clinic.Pagos_Service.DTO.ProcesarPagoRequest;
import com.clinic.Pagos_Service.domain.MetodoPago;

import java.math.BigDecimal;
import java.util.UUID;

public interface PasarelaPagoService {
    String procesarPago(UUID pagoId, BigDecimal monto, MetodoPago metodo, ProcesarPagoRequest request);
    String procesarReembolso(UUID pagoId, BigDecimal monto, String referenciaOriginal);
}