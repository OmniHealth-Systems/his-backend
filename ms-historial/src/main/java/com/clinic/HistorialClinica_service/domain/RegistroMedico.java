package com.clinic.HistorialClinica_service.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "registros_medicos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroMedico {
    public enum TipoRegistro {
        DIAGNOSTICO, TRATAMIENTO, ALERGIA,
        ANTECEDENTE, EXAMEN, VACUNA, NOTA_EVOLUCION
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoRegistro tipo;

    private LocalDateTime fechaRegistro;

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "cita_id")
    private Long citaId;

    @Column(name = "paciente_id")
    private Long pacienteId;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id", nullable = false)
    private HistorialClinico historial;
}