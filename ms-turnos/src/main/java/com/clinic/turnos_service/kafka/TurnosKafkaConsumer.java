package com.clinic.turnos_service.kafka;

import com.clinic.turnos_service.domain.Turno;
import com.clinic.turnos_service.repository.TurnoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Consumidor Kafka de ms-turnos.
 * Escucha eventos de citas para reservar/liberar slots de agenda.
 * Incluye DLQ (Dead Letter Queue) para mensajes que fallan tras reintentos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TurnosKafkaConsumer {

    private final TurnoRepository turnoRepository;
    private final ObjectMapper objectMapper;

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
        try {
            JsonNode node = objectMapper.readTree(message);
            Long citaId = null;
            if (node.has("citaId") && !node.get("citaId").isNull()) {
                citaId = node.get("citaId").asLong();
            } else if (node.has("id") && !node.get("id").isNull()) {
                citaId = node.get("id").asLong();
            }

            if (citaId != null) {
                Optional<Turno> turnoOpt = turnoRepository.findByCitaId(citaId);
                if (turnoOpt.isPresent()) {
                    Turno turno = turnoOpt.get();
                    turno.setEstado(Turno.EstadoTurno.RESERVADO);
                    turnoRepository.save(turno);
                    log.info("[ms-turnos] Turno ID {} marcado como RESERVADO para Cita ID {}", turno.getId(), citaId);
                } else {
                    log.warn("[ms-turnos] No se encontró turno asociado para Cita ID {}", citaId);
                }
            }
        } catch (Exception e) {
            log.error("[ms-turnos] Error procesando evento cita-confirmada: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo al procesar evento cita-confirmada", e);
        }
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
        try {
            JsonNode node = objectMapper.readTree(message);
            Long citaId = null;
            if (node.has("citaId") && !node.get("citaId").isNull()) {
                citaId = node.get("citaId").asLong();
            } else if (node.has("id") && !node.get("id").isNull()) {
                citaId = node.get("id").asLong();
            }

            if (citaId != null) {
                Optional<Turno> turnoOpt = turnoRepository.findByCitaId(citaId);
                if (turnoOpt.isPresent()) {
                    Turno turno = turnoOpt.get();
                    turno.setEstado(Turno.EstadoTurno.DISPONIBLE);
                    turno.setCitaId(null);
                    turnoRepository.save(turno);
                    log.info("[ms-turnos] Turno ID {} liberado a DISPONIBLE tras cancelación de Cita ID {}", turno.getId(), citaId);
                } else {
                    log.warn("[ms-turnos] No se encontró turno asociado para Cita ID cancelada {}", citaId);
                }
            }
        } catch (Exception e) {
            log.error("[ms-turnos] Error procesando evento cita-cancelada: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo al procesar evento cita-cancelada", e);
        }
    }

    /** Handler del DLQ — registra eventos no procesables para inspección manual */
    @KafkaListener(topics = {"citas-confirmadas-dlt", "citas-canceladas-dlt"}, groupId = "turnos-dlq-group")
    public void handleDeadLetterQueue(String failedMessage) {
        log.error("[ms-turnos][DLQ][AUDITORIA] Mensaje enviado a DLQ tras agotar reintentos: {}", failedMessage);
    }
}
