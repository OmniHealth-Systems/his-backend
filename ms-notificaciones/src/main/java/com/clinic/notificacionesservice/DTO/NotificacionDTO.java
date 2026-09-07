package com.clinic.notificacionesservice.DTO;

import com.clinic.notificacionesservice.domain.enums.TipoNotificaciòn;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificacionDTO {
    private TipoNotificaciòn tipo;
    private String contenido;
    private String destinatarioId;
    private String mensaje;
    private LocalDateTime fechaProgramada;
    private String canal;
    private String asunto;
}