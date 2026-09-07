package com.clinic.logistica_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro de entradas/salidas de insumos hospitalarios */
@Entity
@Table(name = "movimientos_stock_insumos")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MovimientoStock {
    public enum TipoMovimiento { INGRESO, EGRESO, AJUSTE, DEVOLUCION, BAJA }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_id", nullable = false)
    private InsumoHospitalario insumo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior")
    private Integer stockAnterior;

    @Column(name = "stock_posterior")
    private Integer stockPosterior;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(name = "motivo")
    private String motivo;

    /** Referencia al evento de origen: "EXAMEN_SOLICITADO", "CIRUGIA", "AJUSTE_MANUAL" */
    @Column(name = "origen_evento")
    private String origenEvento;

    @Column(name = "usuario_responsable_id")
    private Long usuarioResponsableId;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;
}
