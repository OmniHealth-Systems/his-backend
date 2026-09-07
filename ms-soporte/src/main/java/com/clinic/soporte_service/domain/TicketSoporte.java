package com.clinic.soporte_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/** Ticket de soporte clínico y Libro de Reclamaciones (INDECOPI compatible) */
@Entity
@Table(name = "tickets_soporte")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TicketSoporte {
    public enum CategoriaTicket {
        RECLAMO_PACIENTE, INCIDENCIA_TI, FALLA_EQUIPO_BIOMEDICO,
        SOLICITUD_INFORMACION, QUEJA_ATENCION, SUGERENCIA, FAQ_DERIVADO
    }
    public enum PrioridadTicket { BAJA, MEDIA, ALTA, CRITICA }
    public enum EstadoTicket { ABIERTO, EN_PROCESO, PENDIENTE_CLIENTE, RESUELTO, CERRADO, ESCALADO }

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "numero_ticket", unique = true, nullable = false)
    private String numeroTicket;

    @Column(name = "solicitante_id")
    private Long solicitanteId; // paciente o empleado

    @Column(name = "tipo_solicitante")
    private String tipoSolicitante; // "PACIENTE" / "EMPLEADO"

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaTicket categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false)
    private PrioridadTicket prioridad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTicket estado;

    @Column(name = "agente_asignado_id")
    private Long agenteAsignadoId;

    @Column(name = "sede_origen_id")
    private Long sedeOrigenId;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaUltimaActualizacion;

    private LocalDateTime fechaResolucion;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComentarioTicket> comentarios;
}
