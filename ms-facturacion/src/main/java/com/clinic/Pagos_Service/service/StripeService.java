package com.clinic.Pagos_Service.service;

import com.clinic.Pagos_Service.DTO.ProcesarPagoRequest;
import com.clinic.Pagos_Service.domain.MetodoPago;
import com.clinic.Pagos_Service.exception.PasarelaPagoException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class StripeService implements PasarelaPagoService {

    @Override
    @CircuitBreaker(name = "stripeService", fallbackMethod = "fallbackProcesarPago")
    public String procesarPago(UUID pagoId, BigDecimal monto, MetodoPago metodo, ProcesarPagoRequest request) {
        // Simulación de integración con Stripe
        if (metodo != MetodoPago.TARJETA_CREDITO && metodo != MetodoPago.TARJETA_DEBITO) {
            throw new PasarelaPagoException("Stripe solo soporta pagos con tarjeta");
        }

        if (request.getTokenTarjeta() == null) {
            throw new PasarelaPagoException("Token de tarjeta requerido para Stripe");
        }

        // Lógica real de integración con Stripe iría aquí
        System.out.println("Procesando pago con Stripe: " + pagoId + " - " + monto);

        // Simulación de éxito o fallo aleatorio
        if (Math.random() > 0.1) {
            return "ch_" + UUID.randomUUID().toString().replace("-", "");
        } else {
            throw new PasarelaPagoException("Fallo en el procesamiento de la tarjeta");
        }
    }

    @Override
    public String procesarReembolso(UUID pagoId, BigDecimal monto, String referenciaOriginal) {
        // Lógica real de reembolso con Stripe
        System.out.println("Procesando reembolso con Stripe: " + pagoId + " - " + monto);
        return "re_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String fallbackProcesarPago(UUID pagoId, BigDecimal monto, MetodoPago metodo,
                                        ProcesarPagoRequest request, Throwable t) {
        // Lógica de fallback
        System.err.println("Fallback para Stripe: " + t.getMessage());
        return null;
    }
}