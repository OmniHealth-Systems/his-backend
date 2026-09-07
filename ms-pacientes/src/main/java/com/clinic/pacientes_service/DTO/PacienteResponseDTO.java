package com.clinic.pacientes_service.DTO;

import com.clinic.pacientes_service.domain.Direccion;
import com.clinic.pacientes_service.domain.Paciente;
import com.clinic.pacientes_service.domain.SeguroMedico;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PacienteResponseDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private Integer edad;
    private String email;
    private LocalDate fechaNacimiento;
    private Paciente.Genero sexo;
    private Paciente.EstadoCivil estadoCivil;
    private String telefono;
    private String nacionalidad;
    private Direccion direccion;
    private String tipoDocumento;
    private String dni;
    private String contactoEmergencia;
    private SeguroMedico seguroMedico;
    public static PacienteResponseDTO fromEntity(Paciente paciente) {
        return PacienteResponseDTO.builder()
                .id(paciente.getId())
                .nombre(paciente.getNombre())
                .apellidos(paciente.getApellidos())
                .edad(paciente.getEdad())
                .email(paciente.getEmail())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .sexo(paciente.getSexo())
                .estadoCivil(paciente.getEstadoCivil())
                .telefono(paciente.getTelefono())
                .nacionalidad(paciente.getNacionalidad())
                .direccion(paciente.getDireccion())
                .tipoDocumento(paciente.getTipoDocumento())
                .dni(paciente.getDni())
                .contactoEmergencia(paciente.getContactoEmergencia())
                .seguroMedico(paciente.getSeguroMedico())
                .build();
    }
}