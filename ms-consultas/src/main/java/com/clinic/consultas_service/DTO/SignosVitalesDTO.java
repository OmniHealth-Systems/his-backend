package com.clinic.consultas_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignosVitalesDTO {
    private String presionArterial;
    private Integer frecuenciaCardiaca;
    private Double temperatura;
    private Double peso;
    private Double talla;
    private Double imc;
    private Integer saturacionOxigeno;
}
