package com.clinic.farmacia_service.controller;

import com.clinic.farmacia_service.DTO.MedicamentoDTO;
import com.clinic.farmacia_service.DTO.MovimientoStockDTO;
import com.clinic.farmacia_service.service.FarmaciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/farmacia", "/api/farmacia"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FarmaciaController {

    private final FarmaciaService farmaciaService;

    @GetMapping("/medicamentos")
    public ResponseEntity<List<MedicamentoDTO>> listarMedicamentos() {
        return ResponseEntity.ok(farmaciaService.listarMedicamentos());
    }

    @GetMapping("/medicamentos/{id}")
    public ResponseEntity<MedicamentoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(farmaciaService.obtenerPorId(id));
    }

    @PostMapping("/medicamentos")
    public ResponseEntity<MedicamentoDTO> crearMedicamento(@Valid @RequestBody MedicamentoDTO dto) {
        MedicamentoDTO created = farmaciaService.crearMedicamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MedicamentoDTO> registrarMovimiento(@Valid @RequestBody MovimientoStockDTO dto) {
        MedicamentoDTO updated = farmaciaService.registrarMovimiento(dto);
        return ResponseEntity.ok(updated);
    }
}
