package com.clinic.laboratorio_service.controller;

import com.clinic.laboratorio_service.DTO.*;
import com.clinic.laboratorio_service.domain.OrdenLaboratorio;
import com.clinic.laboratorio_service.service.LaboratorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/laboratorio", "/api/laboratorio"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    // --- Catálogo ---
    @GetMapping("/catalogo")
    public ResponseEntity<List<ExamenCatalogoDTO>> listarCatalogo() {
        return ResponseEntity.ok(laboratorioService.listarCatalogo());
    }

    @PostMapping("/catalogo")
    public ResponseEntity<ExamenCatalogoDTO> crearExamenCatalogo(@Valid @RequestBody ExamenCatalogoDTO dto) {
        ExamenCatalogoDTO creado = laboratorioService.crearExamenCatalogo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // --- Órdenes ---
    @PostMapping("/ordenes")
    public ResponseEntity<OrdenLaboratorioResponseDTO> crearOrden(@Valid @RequestBody OrdenLaboratorioRequestDTO request) {
        OrdenLaboratorioResponseDTO orden = laboratorioService.crearOrden(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    @GetMapping("/ordenes")
    public ResponseEntity<List<OrdenLaboratorioResponseDTO>> listarOrdenes() {
        return ResponseEntity.ok(laboratorioService.listarOrdenes());
    }

    @GetMapping("/ordenes/pendientes")
    public ResponseEntity<List<OrdenLaboratorioResponseDTO>> listarOrdenesPendientes() {
        return ResponseEntity.ok(laboratorioService.listarOrdenesPendientes());
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<OrdenLaboratorioResponseDTO> obtenerOrdenPorId(@PathVariable Long id) {
        return ResponseEntity.ok(laboratorioService.obtenerPorId(id));
    }

    @GetMapping("/ordenes/paciente/{pacienteId}")
    public ResponseEntity<List<OrdenLaboratorioResponseDTO>> obtenerOrdenesPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(laboratorioService.obtenerPorPaciente(pacienteId));
    }

    // --- Resultados ---
    @PostMapping("/ordenes/{ordenId}/detalles/{detalleId}/resultado")
    public ResponseEntity<OrdenLaboratorioResponseDTO> registrarResultado(
            @PathVariable Long ordenId,
            @PathVariable Long detalleId,
            @Valid @RequestBody ResultadoLaboratorioDTO resultadoDTO) {
        OrdenLaboratorioResponseDTO response = laboratorioService.registrarResultadoDetalle(ordenId, detalleId, resultadoDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/ordenes/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoOrden(
            @PathVariable Long id,
            @RequestParam OrdenLaboratorio.EstadoOrden estado) {
        laboratorioService.cambiarEstadoOrden(id, estado);
        return ResponseEntity.noContent().build();
    }
}
