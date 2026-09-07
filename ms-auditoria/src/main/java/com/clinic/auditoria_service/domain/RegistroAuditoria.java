package com.clinic.auditoria_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registros_auditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String topicoKafka;

    @Column(nullable = false, length = 100)
    private String eventoTipo;

    @Column(length = 100)
    private String origenMicroservicio;

    private Long usuarioId;
    private Long pacienteId;

    @Column(length = 100)
    private String entidadId;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    @Column(length = 1000)
    private String resumenAccion;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 64)
    private String hashInmutable; // SHA-256 para garantizar inmutabilidad
}
