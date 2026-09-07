package com.clinic.cita_service.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "excepcion_horario")
public class ExcepcionHorario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "franquicia_id", nullable = false)
    private Long franquiciaId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "todo_el_dia", nullable = false)
    private Boolean todoElDia = false;
}
