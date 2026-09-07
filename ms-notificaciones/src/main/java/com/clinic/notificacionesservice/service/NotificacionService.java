package com.clinic.notificacionesservice.service;

import com.clinic.notificacionesservice.DTO.NotificacionDTO;
import com.clinic.notificacionesservice.domain.Notificacion;

// service/NotificacionService.java
public interface NotificacionService {
    Notificacion crearNotificacion(NotificacionDTO dto);
    void enviarNotificacion(Notificacion notificacion);
}
