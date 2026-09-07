package com.clinic.cita_service.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tipo_cita")
public class TipoCita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "duracion_default", nullable = false)
    private Integer duracionDefault;

    @Column(nullable = false)
    private Boolean activo = true;
}