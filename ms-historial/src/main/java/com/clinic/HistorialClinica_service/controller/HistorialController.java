package com.clinic.HistorialClinica_service.controller;

import com.clinic.HistorialClinica_service.DTO.HistorialDTO;
import com.clinic.HistorialClinica_service.service.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialController {
    private final HistorialService historialService;

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<HistorialDTO> obtenerHistorialCompleto(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(historialService.obtenerPorPaciente(pacienteId));
    }
}