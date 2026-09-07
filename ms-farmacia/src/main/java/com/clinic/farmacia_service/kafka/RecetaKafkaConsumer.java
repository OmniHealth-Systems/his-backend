package com.clinic.farmacia_service.kafka;

import com.clinic.farmacia_service.event.RecetaEmitidaEvent;
import com.clinic.farmacia_service.service.FarmaciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecetaKafkaConsumer {

    private final FarmaciaService farmaciaService;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "recetas-emitidas", groupId = "farmacia-group")
    public void consumirRecetaEmitida(RecetaEmitidaEvent event) {
        log.info("Recibido evento RecetaEmitidaEvent desde Kafka: Medicamento='{}', Consulta ID={}, Paciente ID={}", 
                event.getMedicamento(), event.getConsultaId(), event.getPacienteId());
        farmaciaService.deducirStockPorReceta(event);
    }

    @DltHandler
    public void handleDlt(RecetaEmitidaEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("[ms-farmacia][DLQ] Evento enrutado a Dead Letter Queue (DLT) [{}] tras agotar 3 reintentos. Medicamento='{}', Paciente={}",
                topic, event.getMedicamento(), event.getPacienteId());
    }
}
