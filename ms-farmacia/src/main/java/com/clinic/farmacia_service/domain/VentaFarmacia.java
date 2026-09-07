package com.clinic.farmacia_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Venta directa en mostrador (POS) de la farmacia hospitalaria.
 * NUEVA ENTIDAD que resuelve el atajo de "solo deducir Kafka sin módulo de venta".
 */
@Entity
@Table(name = "ventas_farmacia")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VentaFarmacia {
    public enum EstadoVenta { PENDIENTE, COMPLETADA, CANCELADA, DEVUELTA }
    public enum TipoVenta { CON_RECETA, SIN_RECETA, DISPENSACION_INTERNA }

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "numero_venta", unique = true, nullable = false)
    private String numeroVenta;

    @Column(name = "paciente_id")
    private Long pacienteId;

    @Column(name = "receta_id")
    private Long recetaId;

    @Column(name = "cajero_id", nullable = false)
    private Long cajeroId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venta", nullable = false)
    private TipoVenta tipoVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_venta", nullable = false)
    private EstadoVenta estadoVenta;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @CreationTimestamp
    private LocalDateTime fechaVenta;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVentaFarmacia> detalles;
}
