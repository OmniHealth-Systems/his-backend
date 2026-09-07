package com.clinic.Pagos_Service.controller;

import com.clinic.Pagos_Service.DTO.WebhookEvent;
import com.clinic.Pagos_Service.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.net.Webhook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final PagoService pagoService;
    private final ObjectMapper objectMapper;

    public WebhookController(PagoService pagoService, ObjectMapper objectMapper) {
        this.pagoService = pagoService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/stripe")
    public void handleStripeEvent(
            @RequestBody WebhookEvent event,
            @RequestHeader("Stripe-Signature") String signature) {
        if (!validarFirmaStripe(event, signature)) {
            throw new SecurityException("Firma inválida");
        }
        pagoService.procesarEventoStripe(event);
    }

    @PostMapping("/paypal")
    public void handlePayPalEvent(@RequestBody WebhookEvent event) {
        pagoService.procesarEventoPayPal(event);
    }

    private boolean validarFirmaStripe(WebhookEvent event, String signature) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            String webhookSecret = "whsec_tu_secreto"; // Configurar en properties

            // Validación real con SDK de Stripe
            Webhook.constructEvent(payload, signature, webhookSecret);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}