package com.clinic.turnos_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka de ms-turnos.
 * Escucha eventos de citas para reservar/liberar slots de agenda.
 * Incluye DLQ (Dead Letter Queue) para mensajes que fallan tras reintentos.
 */
@Slf4j
@Component
public class TurnosKafkaConsumer {

    /**
     * Escucha confirmaciones de citas para marcar el turno como RESERVADO.
     * DLQ: Si falla 3 veces, el evento va a "citas-confirmadas-dlt".
     */
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "citas-confirmadas", groupId = "turnos-group")
    public void onCitaConfirmada(String message) {
        log.info("[ms-turnos] Evento cita-confirmada recibido: {}", message);
        // TODO: deserializar CitaConfirmadaEvent y marcar Turno como RESERVADO
    }

    /**
     * Escucha cancelaciones de citas para liberar el turno como DISPONIBLE.
     */
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "citas-canceladas", groupId = "turnos-group")
    public void onCitaCancelada(String message) {
        log.info("[ms-turnos] Evento cita-cancelada recibido: {}", message);
        // TODO: deserializar CitaCanceladaEvent y marcar Turno como DISPONIBLE
    }

    /** Handler del DLQ — registra eventos no procesables para inspección manual */
    @KafkaListener(topics = {"citas-confirmadas-dlt", "citas-canceladas-dlt"}, groupId = "turnos-dlq-group")
    public void handleDeadLetterQueue(String failedMessage) {
        log.error("[ms-turnos][DLQ] Mensaje enviado a DLQ tras agotar reintentos: {}", failedMessage);
        // TODO: persistir en tabla dead_letter_events para auditoría
    }
}
