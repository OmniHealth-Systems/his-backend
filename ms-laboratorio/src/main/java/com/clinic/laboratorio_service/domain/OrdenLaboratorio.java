package com.clinic.laboratorio_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordenes_laboratorio")
public class OrdenLaboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pacienteId;

    private Long doctorId;

    private Long consultaId;

    @Column(nullable = false)
    private LocalDateTime fechaOrden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    @Builder.Default
    private EstadoOrden estado = EstadoOrden.PENDIENTE;

    @Column(length = 500)
    private String indicacionesMuestra; // ej. Ayuno 8 horas

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orden_id")
    @Builder.Default
    private List<DetalleOrden> detalles = new ArrayList<>();

    @Column(length = 1000)
    private String observacionesGenerales;

    public enum EstadoOrden {
        PENDIENTE,
        MUESTRA_TOMADA,
        EN_PROCESAMIENTO,
        RESULTADOS_LISTOS,
        ENTREGADA,
        CANCELADA
    }
}
