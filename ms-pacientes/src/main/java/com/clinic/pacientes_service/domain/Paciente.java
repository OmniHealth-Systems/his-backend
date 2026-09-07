package com.clinic.pacientes_service.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "paciente")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    private Integer edad;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero sexo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_civil", nullable = false)
    private EstadoCivil estadoCivil;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String nacionalidad;

    @ManyToOne
    @JoinColumn(name = "id_direccion", nullable = false)
    private Direccion direccion;

    @Column(name = "tipo_documento", nullable = false)
    private String tipoDocumento;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(name = "contacto_emergencia", nullable = false)
    private String contactoEmergencia;

    @ManyToOne
    @JoinColumn(name = "id_seguro_medico", nullable = false)
    private Aseguradora seguroMedico;

    public enum Genero {
        FEMENINO, MASCULINO
    }

    public enum EstadoCivil {
        SOLTERO, CASADO, DIVORCIADO, VIUDo
    }
}