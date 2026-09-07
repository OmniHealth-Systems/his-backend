package com.clinic.farmacia_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Línea de detalle de una venta de farmacia — medicamento + cantidad + precio */
@Entity
@Table(name = "detalle_venta_farmacia")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DetalleVentaFarmacia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private VentaFarmacia venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "requeria_receta")
    private Boolean requieriaReceta;
}
