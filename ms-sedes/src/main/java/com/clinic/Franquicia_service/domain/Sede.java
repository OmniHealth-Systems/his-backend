package com.clinic.Franquicia_service.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "sedes")
public class Sede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String telefono;

    @ElementCollection
    @CollectionTable(name = "sede_especialidades", joinColumns = @JoinColumn(name = "sede_id"))
    @Column(name = "especialidad")
    private List<String> especialidades;

    @Column(nullable = false)
    private String estado; // ACTIVO, INACTIVO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CentroDiagnostico> centrosDiagnostico;
}

