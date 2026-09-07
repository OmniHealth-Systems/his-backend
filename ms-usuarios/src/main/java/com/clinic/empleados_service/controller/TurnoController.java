package com.clinic.empleados_service.controller;

import com.clinic.empleados_service.DTO.TurnoDTO;
import com.clinic.empleados_service.domain.Turno;
import com.clinic.empleados_service.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @PostMapping
    public ResponseEntity<TurnoDTO> createTurno(@Valid @RequestBody TurnoDTO turnoDTO) {
        TurnoDTO createdTurno = turnoService.createTurno(turnoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTurno);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstadoTurno(
            @PathVariable Long id,
            @RequestParam Turno.EstadoTurno estado) {
        turnoService.actualizarEstadoTurno(id, estado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TurnoDTO>> getAllTurnos() {
        return ResponseEntity.ok(turnoService.findAll());
    }
}