package com.clinic.HistorialClinica_service.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Tabla relacional normalizada para alergias del paciente.
 * REEMPLAZA el uso del campo JSON "datosEspecificos" de TipoRegistro.ALERGIA.
 * Permite queries indexadas: "listar todos los pacientes alérgicos a Penicilina".
 */
@Entity
@Table(name = "alergias_paciente")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AlergiaPaciente {
    public enum GravedadAlergia { LEVE, MODERADA, SEVERA, ANAFILACTICA }
    public enum TipoAlergia { MEDICAMENTO, ALIMENTO, AMBIENTAL, CONTACTO, LATEX, OTRO }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id", nullable = false)
    private HistorialClinico historial;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @NotBlank
    @Column(name = "alergeno", nullable = false)
    private String alergeno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alergia", nullable = false)
    private TipoAlergia tipoAlergia;

    @Enumerated(EnumType.STRING)
    @Column(name = "gravedad", nullable = false)
    private GravedadAlergia gravedad;

    @Column(name = "reaccion_clinica", columnDefinition = "TEXT")
    private String reaccionClinica;

    @Column(name = "confirmada")
    @Builder.Default
    private Boolean confirmada = false;

    @Column(name = "doctor_confirmador_id")
    private Long doctorConfirmadorId;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;
}
