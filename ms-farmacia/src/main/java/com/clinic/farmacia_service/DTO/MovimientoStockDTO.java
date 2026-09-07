package com.clinic.farmacia_service.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoStockDTO {

    @NotNull(message = "El ID del medicamento es obligatorio")
    private Long medicamentoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private String tipo; // ENTRADA_COMPRA, SALIDA_MANUAL, AJUSTE_INVENTARIO

    private String motivo;
}
