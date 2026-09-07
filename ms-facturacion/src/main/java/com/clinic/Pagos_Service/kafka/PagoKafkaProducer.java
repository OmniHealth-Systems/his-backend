package com.clinic.Pagos_Service.kafka;

import com.clinic.Pagos_Service.event.PagoRealizadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoKafkaProducer {

    public static final String TOPIC_PAGOS_REALIZADOS = "pagos-realizados";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void emitirPagoRealizado(PagoRealizadoEvent event) {
        try {
            log.info("Emitiendo evento PagoRealizadoEvent para comprobante: {} por monto: {} a topic: {}", 
                    event.getNumeroComprobante(), event.getMonto(), TOPIC_PAGOS_REALIZADOS);
            kafkaTemplate.send(TOPIC_PAGOS_REALIZADOS, event.getPagoId(), event);
        } catch (Exception e) {
            log.error("Error al emitir PagoRealizadoEvent a Kafka: {}", e.getMessage(), e);
        }
    }
}
