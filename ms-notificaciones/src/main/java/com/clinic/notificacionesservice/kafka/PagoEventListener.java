package com.clinic.notificacionesservice.kafka;

import com.clinic.notificacionesservice.DTO.PagoEvent;
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
public class PagoEventListener {

    private final NotificacionService notificacionService;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "pagos-topic", groupId = "notification-group")
    public void handlePagoEvent(PagoEvent event) {
        log.info("Procesando evento de pago para paciente: {}", event.getPacienteId());
        NotificacionDTO dto = new NotificacionDTO();
        dto.setTipo(TipoNotificaciòn.ESTADO_PAGO);
        dto.setDestinatarioId(event.getPacienteId().toString());
        notificacionService.crearNotificacion(dto);
    }

    @DltHandler
    public void handleDlt(PagoEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("[ms-notificaciones][DLQ] Evento PagoEvent falló tras reintentos en [{}], Paciente={}", topic, event.getPacienteId());
    }
}
