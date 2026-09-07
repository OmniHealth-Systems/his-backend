package com.clinic.laboratorio_service.domain;

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
@Table(name = "examenes_catalogo")
public class ExamenCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo; // ej. HEM-001, GLU-002, PER-LIP

    @Column(nullable = false, length = 150)
    private String nombre; // ej. Hemograma Completo, Glucosa en Sangre

    @Column(nullable = false, length = 80)
    private String categoria; // ej. Hematología, Bioquímica, Inmunología, Microbiología

    @Column(nullable = false)
    private Double precio;

    @Column(length = 500)
    private String descripcion;

    private Integer tiempoEntregaHoras; // Tiempo estimado en horas

    @Column(length = 255)
    private String valoresReferenciaDefault;

    @Builder.Default
    private Boolean activo = true;
}
