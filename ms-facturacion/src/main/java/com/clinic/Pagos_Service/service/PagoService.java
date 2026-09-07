package com.clinic.Pagos_Service.service;

import com.clinic.Pagos_Service.DTO.*;
import com.clinic.Pagos_Service.domain.EstadoPago;
import com.clinic.Pagos_Service.domain.MetodoPago;
import com.clinic.Pagos_Service.domain.Pago;
import com.clinic.Pagos_Service.domain.Transaccion;
import com.clinic.Pagos_Service.exception.PagoException;
import com.clinic.Pagos_Service.exception.PagoNotFoundException;
import com.clinic.Pagos_Service.repository.PagoRepository;
import com.clinic.Pagos_Service.repository.TransaccionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final TransaccionRepository transaccionRepository;
    private final StripeService stripeService;
    private final PayPalService payPalService;
    private final ObjectMapper objectMapper;

    @Transactional
    public PagoResponse crearPago(PagoRequest request) {
        Pago pago = Pago.builder()
                .citaId(request.getCitaId())
                .pacienteId(request.getPacienteId())
                .monto(request.getMonto())
                .metodo(request.getMetodo())
                .estado(EstadoPago.PENDIENTE)
                .build();

        try {
            if (request.getDetalles() != null) {
                pago.setDetalles(objectMapper.writeValueAsString(request.getDetalles()));
            }
        } catch (JsonProcessingException e) {
            throw new PagoException("Error al procesar los detalles del pago");
        }

        pago = pagoRepository.save(pago);
        return mapToResponse(pago);
    }

    @Transactional
    public PagoResponse procesarPago(UUID pagoId, ProcesarPagoRequest request) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNotFoundException(pagoId));

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new PagoException("El pago no está en estado pendiente");
        }

        PasarelaPagoService servicioPasarela = obtenerServicioPasarela(pago.getMetodo());

        try {
            String referencia = servicioPasarela.procesarPago(
                    pagoId,
                    pago.getMonto(),
                    pago.getMetodo(),
                    request
            );

            if (referencia == null) {
                throw new PagoException("Fallo en el procesamiento del pago");
            }

            pago.setReferenciaPago(referencia);
            pago.setEstado(EstadoPago.COMPLETADO);
            registrarTransaccion(pago, "PROCESAMIENTO_EXITOSO", "Pago completado");

        } catch (Exception e) {
            pago.setEstado(EstadoPago.FALLIDO);
            registrarTransaccion(pago, "PROCESAMIENTO_FALLIDO", e.getMessage());
            throw new PagoException("Error procesando el pago: " + e.getMessage());
        }

        return mapToResponse(pagoRepository.save(pago));
    }

    @Transactional
    public PagoResponse procesarReembolso(UUID pagoId, ReembolsoRequest request) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNotFoundException(pagoId));

        if (pago.getEstado() != EstadoPago.COMPLETADO) {
            throw new PagoException("Solo se pueden reembolsar pagos completados");
        }

        if (pago.getReferenciaPago() == null) {
            throw new PagoException("Referencia de pago original no encontrada");
        }

        BigDecimal montoReembolso = request.getMontoReembolso();
        if (montoReembolso == null || montoReembolso.compareTo(BigDecimal.ZERO) <= 0) {
            montoReembolso = pago.getMonto(); // Reembolso completo por defecto
        }

        PasarelaPagoService servicioPasarela = obtenerServicioPasarela(pago.getMetodo());

        try {
            String referenciaReembolso = servicioPasarela.procesarReembolso(
                    pagoId,
                    montoReembolso,
                    pago.getReferenciaPago()
            );

            pago.setEstado(EstadoPago.REEMBOLSADO);
            registrarTransaccion(pago, "REEMBOLSO_EXITOSO",
                    "Reembolso de " + montoReembolso + " - Ref: " + referenciaReembolso);

        } catch (Exception e) {
            registrarTransaccion(pago, "REEMBOLSO_FALLIDO", e.getMessage());
            throw new PagoException("Error procesando el reembolso: " + e.getMessage());
        }

        return mapToResponse(pagoRepository.save(pago));
    }

    private PasarelaPagoService obtenerServicioPasarela(MetodoPago metodo) {
        return switch (metodo) {
            case TARJETA_CREDITO, TARJETA_DEBITO -> stripeService;
            case PAYPAL -> payPalService;
            case TRANSFERENCIA, EFECTIVO ->
                    throw new UnsupportedOperationException("Método de pago no implementado: " + metodo);
        };
    }

    private void registrarTransaccion(Pago pago, String accion, String mensaje) {
        Transaccion transaccion = Transaccion.builder()
                .pagoId(pago.getId())
                .accion(accion)
                .mensaje(mensaje)
                .build();

        transaccionRepository.save(transaccion);
    }

    private PagoResponse mapToResponse(Pago pago) {
        Map<String, Object> detalles = null;
        try {
            if (pago.getDetalles() != null) {
                detalles = objectMapper.readValue(pago.getDetalles(), Map.class);
            }
        } catch (JsonProcessingException e) {
            // Log error pero continuar
        }

        return PagoResponse.builder()
                .id(pago.getId())
                .citaId(pago.getCitaId())
                .pacienteId(pago.getPacienteId())
                .monto(pago.getMonto())
                .estado(pago.getEstado())
                .metodo(pago.getMetodo())
                .referenciaPago(pago.getReferenciaPago())
                .fechaCreacion(pago.getFechaCreacion())
                .detalles(detalles)
                .build();
    }
    // En PagoService.java
    public List<PagoResponse> obtenerPagosPorCita(UUID citaId) {
        List<Pago> pagos = pagoRepository.findByCitaId(citaId);
        return pagos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    // En PagoService.java
    public List<PagoResponse> obtenerHistorialPagos(UUID pacienteId, Integer meses) {
        LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(meses != null ? meses : 12);
        List<Pago> pagos = pagoRepository.findByPacienteIdAndFechaCreacionAfter(pacienteId, fechaInicio);
        return pagos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    // En PagoService.java
    public PagoResponse marcarComoPendiente(UUID pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new PagoNotFoundException(pagoId));

        pago.setEstado(EstadoPago.PENDIENTE);
        pago = pagoRepository.save(pago);

        registrarTransaccion(pago, "MARCADO_PENDIENTE", "Pago marcado como pendiente por fallo en pasarela");

        return mapToResponse(pago);
    }
    public void procesarEventoPayPal(WebhookEvent event) {
        String tipoEvento = event.getType();

        switch (tipoEvento) {
            case "PAYMENT.CAPTURE.COMPLETED":
                procesarPagoExitosoPayPal(event);
                break;
            case "PAYMENT.CAPTURE.REFUNDED":
                procesarReembolsoPayPal(event);
                break;
            case "PAYMENT.CAPTURE.DENIED":
                procesarPagoFallidoPayPal(event);
                break;
            default:
                registrarTransaccionWebhook("EVENTO_NO_MANEJADO",
                        "Evento PayPal no manejado: " + tipoEvento);
        }
    }
/* En PagoService.java, después de procesarEventoPayPal */
    public void procesarEventoStripe(WebhookEvent event) {
        String tipoEvento = event.getType();

        switch (tipoEvento) {
            case "payment_intent.succeeded":
                procesarPagoExitosoStripe(event);
                break;
            case "charge.refunded":
                procesarReembolsoStripe(event);
                break;
            case "payment_intent.payment_failed":
                procesarPagoFallidoStripe(event);
                break;
            default:
                registrarTransaccionWebhook("EVENTO_NO_MANEJADO",
                        "Evento Stripe no manejado: " + tipoEvento);
        }
    }
    private void procesarPagoExitosoStripe(WebhookEvent event) {
        Map<String, Object> paymentIntent = (Map<String, Object>) event.getData().get("object");
        String paymentIntentId = (String) paymentIntent.get("id");
        BigDecimal monto = BigDecimal.valueOf((Integer) paymentIntent.get("amount") / 100.0);
        String moneda = (String) paymentIntent.get("currency");

        Pago pago = pagoRepository.findByReferenciaPago(paymentIntentId)
                .orElseGet(() -> {
                    // Crear nuevo pago si no existe
                    Pago nuevo = new Pago();
                    nuevo.setReferenciaPago(paymentIntentId);
                    nuevo.setMonto(monto);
                    nuevo.setMetodo(MetodoPago.TARJETA_CREDITO);
                    return pagoRepository.save(nuevo);
                });

        if (pago.getEstado() != EstadoPago.COMPLETADO) {
            pago.setEstado(EstadoPago.COMPLETADO);
            pagoRepository.save(pago);
            registrarTransaccion(pago, "WEBHOOK_STRIPE", "Pago confirmado");
        }
    }

    private void procesarPagoExitosoPayPal(WebhookEvent event) {
        Map<String, Object> resource = (Map<String, Object>) event.getData().get("resource");
        String captureId = (String) resource.get("id");
        Map<String, Object> amount = (Map<String, Object>) resource.get("amount");
        BigDecimal monto = new BigDecimal((String) amount.get("value"));

        Pago pago = pagoRepository.findByReferenciaPago(captureId)
                .orElseGet(() -> {
                    Pago nuevo = new Pago();
                    nuevo.setReferenciaPago(captureId);
                    nuevo.setMonto(monto);
                    nuevo.setMetodo(MetodoPago.PAYPAL);
                    return pagoRepository.save(nuevo);
                });

        if (pago.getEstado() != EstadoPago.COMPLETADO) {
            pago.setEstado(EstadoPago.COMPLETADO);
            pagoRepository.save(pago);
            registrarTransaccion(pago, "WEBHOOK_PAYPAL", "Pago confirmado");
        }
    }

    private void registrarTransaccionWebhook(String accion, String mensaje) {
        Transaccion transaccion = Transaccion.builder()
                .accion(accion)
                .mensaje(mensaje)
                .build();
        transaccionRepository.save(transaccion);
    }
    private void procesarReembolsoPayPal(WebhookEvent event) {
        Map<String, Object> resource = (Map<String, Object>) event.getData().get("resource");
        String refundId = (String) resource.get("id");
        String captureId = (String) resource.get("capture_id");

        // Buscar el pago original por referencia
        Pago pago = pagoRepository.findByReferenciaPago(captureId)
                .orElseThrow(() -> new PagoException("Pago no encontrado para referencia PayPal: " + captureId));

        // Obtener detalles del reembolso
        Map<String, Object> amount = (Map<String, Object>) resource.get("amount");
        BigDecimal montoReembolso = new BigDecimal((String) amount.get("value"));

        // Actualizar estado del pago
        if (pago.getEstado() != EstadoPago.REEMBOLSADO) {
            pago.setEstado(EstadoPago.REEMBOLSADO);

            // Guardar detalles del reembolso
            try {
                String detalles = "{\"reembolso_id\":\"" + refundId +
                        "\",\"monto_reembolso\":" + montoReembolso + "}";
                pago.setDetalles(detalles);
            } catch (Exception e) {
                // Manejar error
            }

            pagoRepository.save(pago);
            registrarTransaccion(pago, "WEBHOOK_PAYPAL", "Reembolso completado: " + refundId);
        }
    }

    private void procesarPagoFallidoPayPal(WebhookEvent event) {
        Map<String, Object> resource = (Map<String, Object>) event.getData().get("resource");
        String captureId = (String) resource.get("id");

        // Declarar motivo con un valor predeterminado
        String motivo = "Error desconocido";

        // Buscar el pago por referencia
        Optional<Pago> pagoOpt = pagoRepository.findByReferenciaPago(captureId);

        if (pagoOpt.isPresent()) {
            Pago pago = pagoOpt.get();
            if (pago.getEstado() != EstadoPago.FALLIDO) {
                pago.setEstado(EstadoPago.FALLIDO);

                // Obtener motivo del fallo
                Map<String, Object> details = (Map<String, Object>) resource.get("details");
                if (details != null) {
                    motivo = (String) details.get("description");
                }

                try {
                    pago.setDetalles("{\"motivo_fallo\":\"" + motivo + "\"}");
                } catch (Exception e) {
                    // Manejar error
                }

                pagoRepository.save(pago);
                registrarTransaccion(pago, "WEBHOOK_PAYPAL", "Pago fallido: " + motivo);
            }
        } else {
            // Crear nuevo registro si no existe
            Pago nuevoPago = new Pago();
            nuevoPago.setReferenciaPago(captureId);
            nuevoPago.setEstado(EstadoPago.FALLIDO);

            Map<String, Object> amount = (Map<String, Object>) resource.get("amount");
            if (amount != null) {
                BigDecimal monto = new BigDecimal((String) amount.get("value"));
                nuevoPago.setMonto(monto);
            }

            nuevoPago.setMetodo(MetodoPago.PAYPAL);

            Map<String, Object> details = (Map<String, Object>) resource.get("details");
            if (details != null) {
                motivo = (String) details.get("description");
                nuevoPago.setDetalles("{\"motivo_fallo\":\"" + motivo + "\"}");
            } else {
                nuevoPago.setDetalles("{\"motivo_fallo\":\"Error desconocido\"}");
            }

            pagoRepository.save(nuevoPago);
            registrarTransaccion(nuevoPago, "WEBHOOK_PAYPAL", "Pago fallido registrado: " + motivo);
        }
    }

    private void procesarReembolsoStripe(WebhookEvent event) {
        Map<String, Object> charge = (Map<String, Object>) event.getData().get("object");
        String refundId = (String) charge.get("id");
        String paymentIntentId = (String) charge.get("payment_intent");

        // Buscar el pago original
        Pago pago = pagoRepository.findByReferenciaPago(paymentIntentId)
                .orElseThrow(() -> new PagoException("Pago no encontrado para referencia Stripe: " + paymentIntentId));

        // Obtener detalles del reembolso
        BigDecimal montoReembolso = BigDecimal.valueOf((Integer) charge.get("amount_refunded") / 100.0);

        // Actualizar estado del pago
        if (pago.getEstado() != EstadoPago.REEMBOLSADO) {
            pago.setEstado(EstadoPago.REEMBOLSADO);

            // Guardar detalles del reembolso
            try {
                String detalles = "{\"reembolso_id\":\"" + refundId +
                        "\",\"monto_reembolso\":" + montoReembolso + "}";
                pago.setDetalles(detalles);
            } catch (Exception e) {
                // Manejar error
            }

            pagoRepository.save(pago);
            registrarTransaccion(pago, "WEBHOOK_STRIPE", "Reembolso completado: " + refundId);
        }
    }

    private void procesarPagoFallidoStripe(WebhookEvent event) {
        Map<String, Object> paymentIntent = (Map<String, Object>) event.getData().get("object");
        String paymentIntentId = (String) paymentIntent.get("id");

        // Declarar motivo con un valor predeterminado
        String motivo = "Error desconocido";

        // Buscar el pago por referencia
        Optional<Pago> pagoOpt = pagoRepository.findByReferenciaPago(paymentIntentId);

        if (pagoOpt.isPresent()) {
            Pago pago = pagoOpt.get();
            if (pago.getEstado() != EstadoPago.FALLIDO) {
                pago.setEstado(EstadoPago.FALLIDO);

                // Obtener motivo del fallo
                Map<String, Object> lastError = (Map<String, Object>) paymentIntent.get("last_payment_error");
                if (lastError != null) {
                    motivo = (String) lastError.get("message");
                }

                try {
                    pago.setDetalles("{\"motivo_fallo\":\"" + motivo + "\"}");
                } catch (Exception e) {
                    // Manejar error
                }

                pagoRepository.save(pago);
                registrarTransaccion(pago, "WEBHOOK_STRIPE", "Pago fallido: " + motivo);
            }
        } else {
            // Crear nuevo registro si no existe
            Pago nuevoPago = new Pago();
            nuevoPago.setReferenciaPago(paymentIntentId);
            nuevoPago.setEstado(EstadoPago.FALLIDO);

            BigDecimal monto = BigDecimal.valueOf((Integer) paymentIntent.get("amount") / 100.0);
            nuevoPago.setMonto(monto);
            nuevoPago.setMetodo(MetodoPago.TARJETA_CREDITO);

            Map<String, Object> lastError = (Map<String, Object>) paymentIntent.get("last_payment_error");
            if (lastError != null) {
                motivo = (String) lastError.get("message");
                nuevoPago.setDetalles("{\"motivo_fallo\":\"" + motivo + "\"}");
            } else {
                nuevoPago.setDetalles("{\"motivo_fallo\":\"Error desconocido\"}");
            }

            pagoRepository.save(nuevoPago);
            registrarTransaccion(nuevoPago, "WEBHOOK_STRIPE", "Pago fallido registrado: " + motivo);
        }
    }
}