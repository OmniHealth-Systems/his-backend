package com.clinic.farmacia_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private Integer cantidad;

    private Integer stockPrevio;

    private Integer stockPosterior;

    @Column(length = 500)
    private String motivo;

    private Long consultaId; // Vinculación con evento de receta médica

    @Column(nullable = false)
    private LocalDateTime fechaMovimiento;

    public enum TipoMovimiento {
        ENTRADA_COMPRA,
        SALIDA_MANUAL,
        DEDUCCION_RECETA_KAFKA,
        VENTA_POS,
        AJUSTE_INVENTARIO
    }
}
