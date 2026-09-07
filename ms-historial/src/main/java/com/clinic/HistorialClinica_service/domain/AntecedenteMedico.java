package com.clinic.HistorialClinica_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Registro estructurado de antecedentes médicos y familiares del paciente.
 * REEMPLAZA el uso del campo JSON "datosEspecificos" de TipoRegistro.ANTECEDENTE.
 * Incluye antecedentes personales, familiares y quirúrgicos.
 */
@Entity
@Table(name = "antecedentes_medicos")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AntecedenteMedico {
    public enum TipoAntecedente {
        PERSONAL_PATOLOGICO, PERSONAL_NO_PATOLOGICO,
        FAMILIAR, QUIRURGICO, PERINATAL, GINECOLOGICO, PSIQUIATRICO
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id", nullable = false)
    private HistorialClinico historial;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_antecedente", nullable = false)
    private TipoAntecedente tipoAntecedente;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /** Para antecedentes familiares: "PADRE", "MADRE", "HERMANO", etc. */
    @Column(name = "parentesco_familiar")
    private String parentescoFamiliar;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "doctor_registrador_id")
    private Long doctorRegistradorId;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;
}
