package com.clinic.empleados_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "turno")
public class Turno {
    public enum EstadoTurno {
        DISPONIBLE, OCUPADO, CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String lugar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estado;

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Doctor> doctores;
}