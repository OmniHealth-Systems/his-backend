package com.clinic.soporte_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios_ticket")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ComentarioTicket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketSoporte ticket;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(name = "tipo_autor") // "AGENTE" / "PACIENTE" / "SISTEMA"
    private String tipoAutor;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "es_nota_interna")
    private Boolean esNotaInterna = false;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;
}
