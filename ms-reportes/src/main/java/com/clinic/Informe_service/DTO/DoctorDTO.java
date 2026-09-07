package com.clinic.Informe_service.DTO;

import lombok.Data;

@Data
public class DoctorDTO {
    private Long idDoctor;
    private String nombre;
    private String apellido;
    private String cmp;
    private String especialidad;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}