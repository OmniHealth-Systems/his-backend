package com.clinic.consultas_service.domain;

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
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long citaId;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private LocalDateTime fechaConsulta;

    @Column(length = 1000)
    private String motivoConsulta;

    @Column(length = 2000)
    private String anamnesis; // Historia de la enfermedad actual

    @Column(length = 2000)
    private String examenFisico;

    @Embedded
    private SignosVitales signosVitales;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "consulta_id")
    @Builder.Default
    private List<Diagnostico> diagnosticos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "consulta_id")
    @Builder.Default
    private List<Receta> recetas = new ArrayList<>();

    @Column(length = 1000)
    private String planTratamiento;

    private Boolean requiereExamenLaboratorio;

    @Column(length = 500)
    private String examenesSolicitados; // Exámenes clínicos indicados

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoConsulta estado;

    public enum EstadoConsulta {
        EN_ATENCION,
        FINALIZADA,
        ANULADA
    }
}
