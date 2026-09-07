package com.clinic.notificacionesservice.repository;

import com.clinic.notificacionesservice.domain.Notificacion;
import com.clinic.notificacionesservice.domain.enums.EstadoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByEstadoAndFechaProgramadaBefore(EstadoNotificacion estado, LocalDateTime fecha);
}