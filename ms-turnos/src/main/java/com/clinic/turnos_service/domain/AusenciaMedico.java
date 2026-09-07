package com.clinic.turnos_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Ausencias y licencias médicas del personal.
 * Cuando se registra una ausencia, un @KafkaListener bloquea
 * automáticamente los Turnos correspondientes en el rango de fechas.
 */
@Entity
@Table(name = "ausencias_medico")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AusenciaMedico {
    public enum TipoAusencia { VACACIONES, LICENCIA_MEDICA, CAPACITACION, CONGRESO, AUSENCIA_INJUSTIFICADA }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ausencia", nullable = false)
    private TipoAusencia tipoAusencia;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "aprobado")
    private Boolean aprobado = false;
}
