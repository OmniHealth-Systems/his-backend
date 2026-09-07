package com.clinic.Pagos_Service.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobantePagoDTO {
    private String id;
    private String numeroComprobante;

    @NotBlank(message = "El tipo de comprobante es obligatorio (BOLETA/FACTURA)")
    private String tipoComprobante; // BOLETA, FACTURA

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    private Long citaId;
    private Long consultaId;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal montoTotal;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago; // EFECTIVO, TARJETA, YAPE, PLIN, TRANSFERENCIA

    private String estado; // PENDIENTE, PAGADO, ANULADO
    private String descripcion;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaPago;
}
