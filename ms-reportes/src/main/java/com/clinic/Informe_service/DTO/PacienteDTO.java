package com.clinic.Informe_service.DTO;

import lombok.Data;

@Data
public class PacienteDTO {
    private Long idPaciente;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}