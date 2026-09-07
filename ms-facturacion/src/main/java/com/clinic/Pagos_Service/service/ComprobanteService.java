package com.clinic.Pagos_Service.service;

import com.clinic.Pagos_Service.DTO.ComprobantePagoDTO;
import com.clinic.Pagos_Service.domain.ComprobantePago;
import com.clinic.Pagos_Service.event.PagoRealizadoEvent;
import com.clinic.Pagos_Service.kafka.PagoKafkaProducer;
import com.clinic.Pagos_Service.repository.ComprobantePagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprobanteService {

    private final ComprobantePagoRepository comprobanteRepository;
    private final PagoKafkaProducer pagoKafkaProducer;

    public List<ComprobantePagoDTO> listarComprobantes() {
        return comprobanteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ComprobantePagoDTO obtenerPorId(String id) {
        ComprobantePago c = comprobanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante de pago no encontrado con ID: " + id));
        return mapToDTO(c);
    }

    @Transactional
    public ComprobantePagoDTO emitirComprobante(ComprobantePagoDTO dto) {
        String serie = "BOLETA".equalsIgnoreCase(dto.getTipoComprobante()) ? "B001" : "F001";
        String correlativo = String.format("%06d", (int) (comprobanteRepository.count() + 1));
        String numeroGenerado = serie + "-" + correlativo;

        LocalDateTime ahora = LocalDateTime.now();
        boolean pagadoInmediato = "PAGADO".equalsIgnoreCase(dto.getEstado()) || dto.getMetodoPago() != null;

        ComprobantePago comprobante = ComprobantePago.builder()
                .numeroComprobante(numeroGenerado)
                .tipoComprobante(ComprobantePago.TipoComprobante.valueOf(dto.getTipoComprobante().toUpperCase()))
                .pacienteId(dto.getPacienteId())
                .citaId(dto.getCitaId())
                .consultaId(dto.getConsultaId())
                .montoTotal(dto.getMontoTotal())
                .metodoPago(dto.getMetodoPago())
                .estado(pagadoInmediato ? ComprobantePago.EstadoComprobante.PAGADO : ComprobantePago.EstadoComprobante.PENDIENTE)
                .descripcion(dto.getDescripcion() != null ? dto.getDescripcion() : "Atención médica hospitalaria")
                .fechaEmision(ahora)
                .fechaPago(pagadoInmediato ? ahora : null)
                .build();

        ComprobantePago saved = comprobanteRepository.save(comprobante);

        // Si el estado es PAGADO, emitir evento a Kafka
        if (saved.getEstado() == ComprobantePago.EstadoComprobante.PAGADO) {
            PagoRealizadoEvent event = PagoRealizadoEvent.builder()
                    .pagoId(saved.getId())
                    .numeroComprobante(saved.getNumeroComprobante())
                    .tipoComprobante(saved.getTipoComprobante().name())
                    .pacienteId(saved.getPacienteId())
                    .citaId(saved.getCitaId())
                    .monto(saved.getMontoTotal())
                    .metodoPago(saved.getMetodoPago())
                    .estado(saved.getEstado().name())
                    .fechaPago(saved.getFechaPago().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();

            pagoKafkaProducer.emitirPagoRealizado(event);
        }

        return mapToDTO(saved);
    }

    @Transactional
    public ComprobantePagoDTO registrarPagoComprobante(String id, String metodoPago) {
        ComprobantePago c = comprobanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado con ID: " + id));

        c.setEstado(ComprobantePago.EstadoComprobante.PAGADO);
        c.setMetodoPago(metodoPago);
        c.setFechaPago(LocalDateTime.now());

        ComprobantePago saved = comprobanteRepository.save(c);

        // Emitir evento a Kafka
        PagoRealizadoEvent event = PagoRealizadoEvent.builder()
                .pagoId(saved.getId())
                .numeroComprobante(saved.getNumeroComprobante())
                .tipoComprobante(saved.getTipoComprobante().name())
                .pacienteId(saved.getPacienteId())
                .citaId(saved.getCitaId())
                .monto(saved.getMontoTotal())
                .metodoPago(saved.getMetodoPago())
                .estado(saved.getEstado().name())
                .fechaPago(saved.getFechaPago().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        pagoKafkaProducer.emitirPagoRealizado(event);

        return mapToDTO(saved);
    }

    private ComprobantePagoDTO mapToDTO(ComprobantePago c) {
        return ComprobantePagoDTO.builder()
                .id(c.getId())
                .numeroComprobante(c.getNumeroComprobante())
                .tipoComprobante(c.getTipoComprobante().name())
                .pacienteId(c.getPacienteId())
                .citaId(c.getCitaId())
                .consultaId(c.getConsultaId())
                .montoTotal(c.getMontoTotal())
                .metodoPago(c.getMetodoPago())
                .estado(c.getEstado().name())
                .descripcion(c.getDescripcion())
                .fechaEmision(c.getFechaEmision())
                .fechaPago(c.getFechaPago())
                .build();
    }
}
