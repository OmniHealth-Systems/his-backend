package com.clinic.Pagos_Service.DTO;


import com.clinic.Pagos_Service.domain.MetodoPago;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class PagoRequest {
    private UUID citaId;
    private UUID pacienteId;
    private BigDecimal monto;
    private MetodoPago metodo;
    private Map<String, Object> detalles;
}