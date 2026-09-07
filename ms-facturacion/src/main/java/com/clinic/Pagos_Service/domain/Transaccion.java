package com.clinic.Pagos_Service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID pagoId;

    private LocalDateTime fecha;
    private String accion;
    private String codigoRespuesta;
    private String mensaje;
    private String detalles;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }
}