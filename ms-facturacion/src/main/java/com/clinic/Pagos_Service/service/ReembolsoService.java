package com.clinic.Pagos_Service.service;

import com.clinic.Pagos_Service.DTO.ReembolsoRequest;
import com.clinic.Pagos_Service.domain.EstadoPago;
import com.clinic.Pagos_Service.domain.Pago;
import com.clinic.Pagos_Service.exception.PagoException;
import com.clinic.Pagos_Service.exception.PagoNotFoundException;
import com.clinic.Pagos_Service.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReembolsoService {

    private final PagoRepository pagoRepository;
    private final ConciliacionService  conciliacionService;

    @Transactional
    public Pago procesarReembolsoParcial(UUID pagoId, ReembolsoRequest request) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNotFoundException(pagoId));

        if (pago.getEstado() != EstadoPago.COMPLETADO) {
            throw new PagoException("Solo se pueden reembolsar pagos completados");
        }

        BigDecimal montoReembolso = request.getMontoReembolso();
        if (montoReembolso == null || montoReembolso.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagoException("Monto de reembolso inválido");
        }

        if (montoReembolso.compareTo(pago.getMonto()) > 0) {
            throw new PagoException("Monto de reembolso excede el pago original");
        }

        // Lógica específica de reembolso parcial
        // (En una implementación real, aquí se integraría con la pasarela de pagos)

        pago.setEstado(EstadoPago.REEMBOLSADO);
        pago.setDetalles("{\"reembolso_parcial\": " + montoReembolso +
                ", \"motivo\": \"" + request.getMotivo() + "\"}");

        // Registrar en conciliación
        conciliacionService.registrarReembolso(pagoId, montoReembolso);

        return pagoRepository.save(pago);
    }
}