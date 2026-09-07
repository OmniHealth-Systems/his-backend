package com.clinic.Pagos_Service.controller;

import com.clinic.Pagos_Service.DTO.ComprobantePagoDTO;
import com.clinic.Pagos_Service.service.ComprobanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"/api/v1/facturacion", "/api/facturacion"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FacturacionController {

    private final ComprobanteService comprobanteService;

    @GetMapping("/comprobantes")
    public ResponseEntity<List<ComprobantePagoDTO>> listarComprobantes() {
        return ResponseEntity.ok(comprobanteService.listarComprobantes());
    }

    @GetMapping("/comprobantes/{id}")
    public ResponseEntity<ComprobantePagoDTO> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(comprobanteService.obtenerPorId(id));
    }

    @PostMapping("/comprobantes")
    public ResponseEntity<ComprobantePagoDTO> emitirComprobante(@Valid @RequestBody ComprobantePagoDTO dto) {
        ComprobantePagoDTO emitido = comprobanteService.emitirComprobante(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(emitido);
    }

    @PostMapping("/comprobantes/{id}/pagar")
    public ResponseEntity<ComprobantePagoDTO> registrarPago(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        String metodoPago = payload.getOrDefault("metodoPago", "EFECTIVO");
        ComprobantePagoDTO pagado = comprobanteService.registrarPagoComprobante(id, metodoPago);
        return ResponseEntity.ok(pagado);
    }
}
