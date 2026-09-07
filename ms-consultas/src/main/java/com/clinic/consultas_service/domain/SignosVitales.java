package com.clinic.consultas_service.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SignosVitales {
    private String presionArterial; // ej. 120/80 mmHg
    private Integer frecuenciaCardiaca; // ej. 75 bpm
    private Double temperatura; // ej. 36.5 °C
    private Double peso; // ej. 70.5 kg
    private Double talla; // ej. 172.0 cm
    private Double imc; // Índice de Masa Corporal
    private Integer saturacionOxigeno; // ej. 98 %
}
