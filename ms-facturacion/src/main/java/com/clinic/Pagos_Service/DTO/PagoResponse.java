package com.clinic.Pagos_Service.DTO;

import com.clinic.Pagos_Service.domain.EstadoPago;
import com.clinic.Pagos_Service.domain.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {
    private UUID id;
    private UUID citaId;
    private UUID pacienteId;
    private BigDecimal monto;
    private EstadoPago estado;
    private MetodoPago metodo;
    private String referenciaPago;
    private LocalDateTime fechaCreacion;
    private Map<String, Object> detalles;
}