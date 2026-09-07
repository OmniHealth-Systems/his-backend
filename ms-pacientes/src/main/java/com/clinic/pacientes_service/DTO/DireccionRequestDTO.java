package com.clinic.pacientes_service.DTO;
import lombok.Data;

@Data
public class DireccionRequestDTO {
    private String departamento;
    private String provincia;
    private String ciudad;
}