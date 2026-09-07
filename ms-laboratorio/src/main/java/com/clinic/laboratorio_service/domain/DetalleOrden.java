package com.clinic.laboratorio_service.domain;

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
@Table(name = "detalles_orden_laboratorio")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examen_id", nullable = false)
    private ExamenCatalogo examen;

    @Column(length = 500)
    private String resultado; // Valor medido (ej. 95 mg/dL, 4.5 millones/uL)

    @Column(length = 255)
    private String valoresReferencia;

    @Column(length = 500)
    private String observaciones;

    private LocalDateTime fechaResultado;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EstadoDetalle estado = EstadoDetalle.PENDIENTE;

    public enum EstadoDetalle {
        PENDIENTE,
        EN_ANALISIS,
        COMPLETADO,
        RECHAZADO
    }
}
