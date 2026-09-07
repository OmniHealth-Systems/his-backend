package com.clinic.turnos_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Agenda semanal de un médico: define los días y horarios en que atiende.
 * Desacoplada de ms-usuarios para permitir escalabilidad independiente.
 */
@Entity
@Table(name = "agendas_medicas", uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "dia_semana"}))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AgendaMedica {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "duracion_slot_minutos", nullable = false)
    private Integer duracionSlotMinutos;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Turno> turnos;
}
