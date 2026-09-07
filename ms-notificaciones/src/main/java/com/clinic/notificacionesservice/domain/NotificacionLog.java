package com.clinic.notificacionesservice.domain;
import com.clinic.notificacionesservice.domain.enums.ResultadoEnvio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "notification_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notificacion_id")
    private Notificacion notificacion;

    @Enumerated(EnumType.STRING)
    private ResultadoEnvio resultado;

    private String mensajeError;
    private LocalDateTime fecha;
}
