package com.clinic.empleados_service.controller;

import com.clinic.empleados_service.DTO.EspecialidadDTO;
import com.clinic.empleados_service.service.EspecialidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @PostMapping
    public ResponseEntity<EspecialidadDTO> createEspecialidad(@Valid @RequestBody EspecialidadDTO especialidadDTO) {
        EspecialidadDTO createdEspecialidad = especialidadService.createEspecialidad(especialidadDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEspecialidad);
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> getAllEspecialidades() {
        return ResponseEntity.ok(especialidadService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> getEspecialidadById(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.findById(id));
    }
}