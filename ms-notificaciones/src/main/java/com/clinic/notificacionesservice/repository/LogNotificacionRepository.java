package com.clinic.notificacionesservice.repository;

import com.clinic.notificacionesservice.domain.NotificacionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogNotificacionRepository extends JpaRepository<NotificacionLog, Long> {}
