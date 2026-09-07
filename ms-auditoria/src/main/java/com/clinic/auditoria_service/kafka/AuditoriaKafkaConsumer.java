package com.clinic.auditoria_service.kafka;

import com.clinic.auditoria_service.domain.RegistroAuditoria;
import com.clinic.auditoria_service.repository.RegistroAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaKafkaConsumer {

    private final RegistroAuditoriaRepository auditoriaRepository;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = {"examenes-solicitados", "recetas-emitidas", "pagos-realizados", "citas-eventos", "notificaciones-eventos"},
            groupId = "auditoria-maestra-group"
    )
    public void auditarEvento(ConsumerRecord<String, String> record) {
        log.info("Auditoría Maestra capturó evento en tópico [{}], key: {}", record.topic(), record.key());

        try {
            LocalDateTime timestamp = LocalDateTime.now();
            String payload = record.value();

            // Generar hash inmutable SHA-256 (Payload + Timestamp + Tópico)
            String rawToHash = record.topic() + ":" + record.key() + ":" + payload + ":" + timestamp;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToHash.getBytes(StandardCharsets.UTF_8));
            String hash = HexFormat.of().formatHex(hashBytes);

            RegistroAuditoria registro = RegistroAuditoria.builder()
                    .topicoKafka(record.topic())
                    .eventoTipo(record.topic().replace("-", "_").toUpperCase())
                    .entidadId(record.key())
                    .payloadJson(payload)
                    .resumenAccion("Evento procesado en Confluent Cloud Topic [" + record.topic() + "] con Partition " + record.partition())
                    .timestamp(timestamp)
                    .hashInmutable(hash)
                    .build();

            auditoriaRepository.save(registro);
            log.info("Registro de auditoría #{} guardado con hash inmutable: {}", registro.getId(), hash);
        } catch (Exception e) {
            log.error("Error al registrar auditoría inmutable: {}", e.getMessage(), e);
            throw new RuntimeException("Error en persistencia de auditoría", e);
        }
    }

    @DltHandler
    public void handleDlt(ConsumerRecord<String, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String dltTopic) {
        log.error("[ms-auditoria][DLQ] Evento inauditable enviado a DLT [{}] Tópico original={}, key={}",
                dltTopic, record.topic(), record.key());
    }
}
