package com.clinic.Franquicia_service.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "centros_diagnostico")
public class CentroDiagnostico {
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
    @CollectionTable(name = "centro_servicios", joinColumns = @JoinColumn(name = "centro_id"))
    @Column(name = "servicio")
    private List<String> servicios;

    @Column(nullable = false)
    private String estado; // ACTIVO, INACTIVO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;
}

