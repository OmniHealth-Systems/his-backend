package com.clinic.consultas_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recetas")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String medicamento;

    @Column(nullable = false, length = 80)
    private String dosis; // ej. 500 mg

    @Column(nullable = false, length = 80)
    private String frecuencia; // ej. Cada 8 horas

    @Column(nullable = false, length = 80)
    private String duracion; // ej. Por 7 días

    @Column(length = 500)
    private String indicaciones; // ej. Tomar después de las comidas
}
