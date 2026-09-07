package com.clinic.HistorialClinica_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registro estructurado de vacunas aplicadas al paciente.
 * REEMPLAZA el uso del campo JSON "datosEspecificos" de TipoRegistro.VACUNA.
 */
@Entity
@Table(name = "vacunas_paciente")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VacunaPaciente {
    public enum EstadoVacuna { APLICADA, PENDIENTE, CONTRAINDICADA, ESQUEMA_INCOMPLETO }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id", nullable = false)
    private HistorialClinico historial;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Column(name = "nombre_vacuna", nullable = false)
    private String nombreVacuna;

    @Column(name = "dosis")
    private String dosis;

    @Column(name = "numero_dosis_aplicada")
    private Integer numeroDosisAplicada;

    @Column(name = "total_dosis_esquema")
    private Integer totalDosisEsquema;

    @Column(name = "fabricante")
    private String fabricante;

    @Column(name = "lote")
    private String lote;

    @Column(name = "fecha_aplicacion")
    private LocalDate fechaAplicacion;

    @Column(name = "fecha_proxima_dosis")
    private LocalDate fechaProximaDosis;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoVacuna estado;

    @Column(name = "doctor_aplicador_id")
    private Long doctorAplicadorId;

    @Column(name = "sede_id")
    private Long sedeId;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;
}
