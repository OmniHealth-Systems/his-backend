package com.clinic.notificacionesservice.domain;

import com.clinic.notificacionesservice.domain.enums.EstadoNotificacion;
import com.clinic.notificacionesservice.domain.enums.TipoNotificaciòn;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoNotificaciòn tipo;

    private String contenido;
    private String destinatarioId;

    @Enumerated(EnumType.STRING)
    private EstadoNotificacion estado;

    private LocalDateTime fechaEnvio;
    private String mensaje;
    private LocalDateTime fechaProgramada;
    private String canal;
    private String asunto;
}