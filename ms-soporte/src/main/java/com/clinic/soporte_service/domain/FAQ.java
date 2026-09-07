package com.clinic.soporte_service.domain;

import jakarta.persistence.*;
import lombok.*;

/** Base de conocimiento / FAQ para auto-servicio de pacientes */
@Entity
@Table(name = "faq")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FAQ {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pregunta", nullable = false, columnDefinition = "TEXT")
    private String pregunta;

    @Column(name = "respuesta", nullable = false, columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "orden_visualizacion")
    private Integer ordenVisualizacion;
}
