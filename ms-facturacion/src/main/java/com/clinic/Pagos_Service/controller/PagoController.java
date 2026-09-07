package com.clinic.Pagos_Service.controller;

import com.clinic.Pagos_Service.DTO.PagoRequest;
import com.clinic.Pagos_Service.DTO.PagoResponse;
import com.clinic.Pagos_Service.DTO.ProcesarPagoRequest;
import com.clinic.Pagos_Service.DTO.ReembolsoRequest;
import com.clinic.Pagos_Service.service.PagoService;
import com.clinic.Pagos_Service.service.ReembolsoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final ReembolsoService reembolsoService;

    @PostMapping
    public ResponseEntity<PagoResponse> crearPago(@RequestBody PagoRequest request) {
        return ResponseEntity.ok(pagoService.crearPago(request));
    }

    @PostMapping("/{id}/procesar")
    @CircuitBreaker(name = "procesarPago", fallbackMethod = "fallbackProcesarPago")
    public ResponseEntity<PagoResponse> procesarPago(
            @PathVariable UUID id,
            @RequestBody ProcesarPagoRequest request) {
        return ResponseEntity.ok(pagoService.procesarPago(id, request));
    }

    @PostMapping("/{id}/reembolso")
    public ResponseEntity<PagoResponse> procesarReembolso(
            @PathVariable UUID id,
            @RequestBody ReembolsoRequest request) {
        return ResponseEntity.ok(pagoService.procesarReembolso(id, request));
    }

    @GetMapping("/cita/{citaId}")
    public ResponseEntity<List<PagoResponse>> obtenerPagosPorCita(@PathVariable UUID citaId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorCita(citaId));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<PagoResponse>> obtenerHistorialPagos(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) Integer meses) {
        return ResponseEntity.ok(pagoService.obtenerHistorialPagos(pacienteId, meses));
    }

    // Fallback para procesamiento de pagos
    public ResponseEntity<PagoResponse> fallbackProcesarPago(
            UUID id, ProcesarPagoRequest request, Throwable t) {
        // Lógica de fallback: registrar pago como pendiente para reintento posterior
        PagoResponse response = pagoService.marcarComoPendiente(id);
        return ResponseEntity.status(202).body(response);
    }
}