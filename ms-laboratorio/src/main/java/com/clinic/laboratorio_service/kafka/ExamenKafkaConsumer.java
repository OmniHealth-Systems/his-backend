package com.clinic.laboratorio_service.kafka;

import com.clinic.laboratorio_service.domain.DetalleOrden;
import com.clinic.laboratorio_service.domain.ExamenCatalogo;
import com.clinic.laboratorio_service.domain.OrdenLaboratorio;
import com.clinic.laboratorio_service.event.ExamenSolicitadoEvent;
import com.clinic.laboratorio_service.repository.ExamenCatalogoRepository;
import com.clinic.laboratorio_service.repository.OrdenLaboratorioRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamenKafkaConsumer {

    private final OrdenLaboratorioRepository ordenRepository;
    private final ExamenCatalogoRepository examenRepository;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        dltTopicSuffix = "-dlt"
    )
    @Transactional
    @KafkaListener(topics = "examenes-solicitados", groupId = "laboratorio-group")
    public void consumirExamenSolicitado(ExamenSolicitadoEvent event) {
        log.info("Consumiendo evento ExamenSolicitadoEvent desde Confluent Kafka para Consulta ID: {}, Paciente ID: {}", 
                event.getConsultaId(), event.getPacienteId());

        List<DetalleOrden> detalles = new ArrayList<>();

        if (event.getExamenesIds() != null && !event.getExamenesIds().isEmpty()) {
            for (Long examenId : event.getExamenesIds()) {
                examenRepository.findById(examenId).ifPresent(examen -> {
                    detalles.add(DetalleOrden.builder()
                            .examen(examen)
                            .valoresReferencia(examen.getValoresReferenciaDefault())
                            .estado(DetalleOrden.EstadoDetalle.PENDIENTE)
                            .build());
                });
            }
        } else {
            List<ExamenCatalogo> catalogo = examenRepository.findAll();
            ExamenCatalogo defaultExamen = catalogo.isEmpty() ? null : catalogo.get(0);
            detalles.add(DetalleOrden.builder()
                    .examen(defaultExamen)
                    .observaciones("Solicitud médica: " + event.getExamenesNombres())
                    .estado(DetalleOrden.EstadoDetalle.PENDIENTE)
                    .build());
        }

        OrdenLaboratorio orden = OrdenLaboratorio.builder()
                .pacienteId(event.getPacienteId())
                .doctorId(event.getDoctorId())
                .consultaId(event.getConsultaId())
                .fechaOrden(LocalDateTime.now())
                .estado(OrdenLaboratorio.EstadoOrden.PENDIENTE)
                .indicacionesMuestra(event.getIndicacionesClinicas())
                .observacionesGenerales("Orden generada automáticamente por evento Kafka desde Consulta Médica #" + event.getConsultaId())
                .detalles(detalles)
                .build();

        ordenRepository.save(orden);
        log.info("Orden de laboratorio #{} creada con éxito en estado PENDIENTE", orden.getId());
    }

    @DltHandler
    public void handleDlt(ExamenSolicitadoEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("[ms-laboratorio][DLQ] Evento enrutado a Dead Letter Queue (DLT) [{}] tras agotar 3 reintentos. Consulta ID={}, Paciente ID={}",
                topic, event.getConsultaId(), event.getPacienteId());
    }
}
