package com.clinic.logistica_service.controller;

import com.clinic.logistica_service.domain.InsumoHospitalario;
import com.clinic.logistica_service.domain.MovimientoStock;
import com.clinic.logistica_service.service.LogisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logistica")
@RequiredArgsConstructor
public class LogisticaController {

    private final LogisticaService logisticaService;

    @GetMapping("/insumos")
    public ResponseEntity<List<InsumoHospitalario>> listarInsumos() {
        return ResponseEntity.ok(logisticaService.listarInsumos());
    }

    @GetMapping("/insumos/sede/{sedeId}")
    public ResponseEntity<List<InsumoHospitalario>> listarPorSede(@PathVariable Long sedeId) {
        return ResponseEntity.ok(logisticaService.listarPorSede(sedeId));
    }

    @GetMapping("/insumos/alertas")
    public ResponseEntity<List<InsumoHospitalario>> alertasStockMinimo() {
        return ResponseEntity.ok(logisticaService.listarAlertasStockMinimo());
    }

    @PostMapping("/insumos")
    public ResponseEntity<InsumoHospitalario> registrarInsumo(@RequestBody InsumoHospitalario insumo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logisticaService.guardarInsumo(insumo));
    }

    @PostMapping("/insumos/{id}/movimiento")
    public ResponseEntity<InsumoHospitalario> registrarMovimiento(
            @PathVariable Long id,
            @RequestParam int cantidad,
            @RequestParam MovimientoStock.TipoMovimiento tipo,
            @RequestParam(required = false) String motivo,
            @RequestParam(required = false) Long sedeId,
            @RequestParam(required = false) Long usuarioId
    ) {
        return ResponseEntity.ok(logisticaService.registrarMovimiento(id, cantidad, tipo, motivo, sedeId, usuarioId));
    }
}
