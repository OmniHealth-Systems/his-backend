package com.clinic.consultas_service.kafka;

import com.clinic.consultas_service.event.ExamenSolicitadoEvent;
import com.clinic.consultas_service.event.RecetaEmitidaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamenKafkaProducer {

    public static final String TOPIC_EXAMENES_SOLICITADOS = "examenes-solicitados";
    public static final String TOPIC_RECETAS_EMITIDAS = "recetas-emitidas";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void emitirExamenSolicitado(ExamenSolicitadoEvent event) {
        try {
            log.info("Emitiendo evento ExamenSolicitadoEvent para consulta ID: {} a Confluent Cloud topic: {}", 
                    event.getConsultaId(), TOPIC_EXAMENES_SOLICITADOS);
            kafkaTemplate.send(TOPIC_EXAMENES_SOLICITADOS, String.valueOf(event.getConsultaId()), event);
        } catch (Exception e) {
            log.error("Error al emitir ExamenSolicitadoEvent a Kafka: {}", e.getMessage(), e);
        }
    }

    public void emitirRecetaEmitida(RecetaEmitidaEvent event) {
        try {
            log.info("Emitiendo evento RecetaEmitidaEvent para medicamento: '{}' en consulta ID: {} a topic: {}", 
                    event.getMedicamento(), event.getConsultaId(), TOPIC_RECETAS_EMITIDAS);
            kafkaTemplate.send(TOPIC_RECETAS_EMITIDAS, String.valueOf(event.getConsultaId()), event);
        } catch (Exception e) {
            log.error("Error al emitir RecetaEmitidaEvent a Kafka: {}", e.getMessage(), e);
        }
    }
}
