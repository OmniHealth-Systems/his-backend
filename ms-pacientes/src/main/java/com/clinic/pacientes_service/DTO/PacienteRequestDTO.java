package com.clinic.pacientes_service.DTO;

import com.clinic.pacientes_service.domain.Paciente;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 50, message = "Los apellidos deben tener entre 2 y 50 caracteres")
    private String apellidos;

    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 130, message = "La edad no puede ser superior a 130")
    private Integer edad;

    @Email(message = "El formato de correo electrónico es inválido")
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El sexo es obligatorio")
    private Paciente.Genero sexo;

    private Paciente.EstadoCivil estadoCivil;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9+ ]{6,15}$", message = "El teléfono debe contener entre 6 y 15 dígitos")
    private String telefono;

    private String nacionalidad;

    private Long direccionId;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El DNI o documento de identidad es obligatorio")
    @Pattern(regexp = "^[0-9A-Za-z]{8,12}$", message = "El documento debe tener entre 8 y 12 caracteres")
    private String dni;

    private String contactoEmergencia;

    private Long seguroMedicoId;
}