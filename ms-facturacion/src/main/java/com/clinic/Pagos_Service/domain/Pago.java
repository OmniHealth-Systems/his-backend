package com.clinic.Pagos_Service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID citaId;

    @Column(nullable = false)
    private UUID pacienteId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodo;

    private String referenciaPago;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Column(columnDefinition = "TEXT")
    private String detalles;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}