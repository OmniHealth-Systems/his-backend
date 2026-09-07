package com.clinic.turnos_service.controller;

import com.clinic.turnos_service.domain.Turno;
import com.clinic.turnos_service.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping("/doctor/{doctorId}/disponibles")
    public ResponseEntity<List<Turno>> obtenerDisponibles(@PathVariable Long doctorId) {
        return ResponseEntity.ok(turnoService.obtenerTurnosDisponibles(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/disponible")
    public ResponseEntity<Boolean> verificarDisponibilidad(@PathVariable Long doctorId) {
        return ResponseEntity.ok(turnoService.verificarDisponibilidadDoctor(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/rango")
    public ResponseEntity<List<Turno>> obtenerPorRango(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin
    ) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorRango(doctorId, inicio, fin));
    }

    @PostMapping("/{turnoId}/reservar")
    public ResponseEntity<Turno> reservarTurno(@PathVariable Long turnoId, @RequestParam Long citaId) {
        return ResponseEntity.ok(turnoService.reservarTurno(turnoId, citaId));
    }

    @PostMapping("/liberar-por-cita/{citaId}")
    public ResponseEntity<Turno> liberarTurno(@PathVariable Long citaId) {
        return ResponseEntity.ok(turnoService.liberarTurno(citaId));
    }
}
