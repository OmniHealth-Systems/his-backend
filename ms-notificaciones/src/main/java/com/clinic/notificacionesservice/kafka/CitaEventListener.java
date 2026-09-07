package com.clinic.notificacionesservice.kafka;

import com.clinic.notificacionesservice.DTO.CitaEvent;
import com.clinic.notificacionesservice.DTO.NotificacionDTO;
import com.clinic.notificacionesservice.domain.enums.TipoNotificaciòn;
import com.clinic.notificacionesservice.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CitaEventListener {

    private final NotificacionService notificacionService;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "citas-topic", groupId = "notification-group")
    public void handleCitaEvent(CitaEvent event) {
        log.info("Procesando evento CitaEvent tipo {} para paciente: {}", event.getTipoEvento(), event.getPacienteId());
        if ("CREATED".equals(event.getTipoEvento())) {
            NotificacionDTO dto = new NotificacionDTO();
            dto.setTipo(TipoNotificaciòn.CONFIRMACION_CITA);
            dto.setDestinatarioId(event.getPacienteId().toString());
            notificacionService.crearNotificacion(dto);

            NotificacionDTO dtoDoctor = new NotificacionDTO();
            dtoDoctor.setTipo(TipoNotificaciòn.RECORDATORIO_CITA);
            if (event.getDoctorId() != null) {
                dtoDoctor.setDestinatarioId(event.getDoctorId().toString());
            }
            notificacionService.crearNotificacion(dtoDoctor);
        }
    }

    @DltHandler
    public void handleDlt(CitaEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("[ms-notificaciones][DLQ] Evento CitaEvent falló tras reintentos en [{}], CitaId={}", topic, event.getCitaId());
    }
}