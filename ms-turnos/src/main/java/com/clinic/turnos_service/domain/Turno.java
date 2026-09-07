package com.clinic.turnos_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Slot de tiempo concreto en la agenda de un médico.
 * Estado: DISPONIBLE → RESERVADO (por ms-citas vía Kafka) → COMPLETADO / CANCELADO.
 */
@Entity
@Table(name = "turnos")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Turno {
    public enum EstadoTurno { DISPONIBLE, RESERVADO, COMPLETADO, CANCELADO, BLOQUEADO }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false)
    private AgendaMedica agenda;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    /** ID de la cita reservada en ms-citas (FK lógica cross-service) */
    @Column(name = "cita_id")
    private Long citaId;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTurno estado;

    @Column(name = "motivo_bloqueo")
    private String motivoBloqueo;
}
