package com.clinic.consultas_service.controller;

import com.clinic.consultas_service.DTO.ConsultaRequestDTO;
import com.clinic.consultas_service.DTO.ConsultaResponseDTO;
import com.clinic.consultas_service.service.ConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/consultas", "/api/consultas"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> registrarConsulta(@Valid @RequestBody ConsultaRequestDTO request) {
        ConsultaResponseDTO response = consultaService.registrarConsulta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listarConsultas() {
        return ResponseEntity.ok(consultaService.listarConsultas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.obtenerPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ConsultaResponseDTO>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(consultaService.obtenerPorPaciente(pacienteId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ConsultaResponseDTO>> obtenerPorDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(consultaService.obtenerPorDoctor(doctorId));
    }
}
