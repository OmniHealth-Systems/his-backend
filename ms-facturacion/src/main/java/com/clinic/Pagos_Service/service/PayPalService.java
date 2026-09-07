package com.clinic.Pagos_Service.service;

import com.clinic.Pagos_Service.DTO.ProcesarPagoRequest;
import com.clinic.Pagos_Service.domain.MetodoPago;
import com.clinic.Pagos_Service.exception.PasarelaPagoException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PayPalService implements PasarelaPagoService {

    @Override
    @CircuitBreaker(name = "paypalService", fallbackMethod = "fallbackProcesarPago")
    public String procesarPago(UUID pagoId, BigDecimal monto, MetodoPago metodo, ProcesarPagoRequest request) {
        // Simulación de integración con PayPal
        if (metodo != MetodoPago.PAYPAL) {
            throw new PasarelaPagoException("PayPalService solo soporta pagos con PayPal");
        }

        if (request.getEmailPayPal() == null) {
            throw new PasarelaPagoException("Email de PayPal requerido");
        }

        // Lógica real de integración con PayPal iría aquí
        System.out.println("Procesando pago con PayPal: " + pagoId + " - " + monto);

        // Simulación de éxito o fallo aleatorio
        if (Math.random() > 0.15) {
            return "PAYID-" + UUID.randomUUID().toString().toUpperCase().substring(0, 17);
        } else {
            throw new PasarelaPagoException("Fallo en la autenticación de PayPal");
        }
    }

    @Override
    public String procesarReembolso(UUID pagoId, BigDecimal monto, String referenciaOriginal) {
        // Lógica real de reembolso con PayPal
        System.out.println("Procesando reembolso con PayPal: " + pagoId + " - " + monto);
        return "REFUND-" + UUID.randomUUID().toString().toUpperCase().substring(0, 10);
    }

    private String fallbackProcesarPago(UUID pagoId, BigDecimal monto, MetodoPago metodo,
                                        ProcesarPagoRequest request, Throwable t) {
        // Lógica de fallback
        System.err.println("Fallback para PayPal: " + t.getMessage());
        return null;
    }
}