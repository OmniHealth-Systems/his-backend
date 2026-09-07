package com.clinic.notificacionesservice.scheduler;

import com.clinic.notificacionesservice.domain.Notificacion;
import com.clinic.notificacionesservice.domain.enums.EstadoNotificacion;
import com.clinic.notificacionesservice.repository.NotificacionRepository;
import com.clinic.notificacionesservice.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecordatorioScheduler {

    private final NotificacionService notificacionService;
    private final NotificacionRepository notificacionRepository;

    @Scheduled(cron = "0 0 8 * * ?") // Ejecutar diariamente a las 8 AM
    public void enviarRecordatorios() {
        LocalDateTime manana = LocalDateTime.now().plusDays(1);
        List<Notificacion> recordatorios = notificacionRepository
                .findByEstadoAndFechaProgramadaBefore(EstadoNotificacion.PENDIENTE, manana);

        recordatorios.forEach(notificacionService::enviarNotificacion);
    }
}