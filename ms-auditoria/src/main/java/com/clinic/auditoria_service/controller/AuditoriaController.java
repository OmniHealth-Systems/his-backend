package com.clinic.auditoria_service.controller;

import com.clinic.auditoria_service.domain.RegistroAuditoria;
import com.clinic.auditoria_service.repository.RegistroAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/auditoria", "/api/auditoria"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuditoriaController {

    private final RegistroAuditoriaRepository auditoriaRepository;

    @GetMapping("/logs")
    public ResponseEntity<List<RegistroAuditoria>> listarLogs() {
        return ResponseEntity.ok(auditoriaRepository.findTop100ByOrderByTimestampDesc());
    }

    @GetMapping("/logs/paciente/{pacienteId}")
    public ResponseEntity<List<RegistroAuditoria>> listarLogsPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(auditoriaRepository.findByPacienteIdOrderByTimestampDesc(pacienteId));
    }

    @GetMapping("/logs/topico/{topico}")
    public ResponseEntity<List<RegistroAuditoria>> listarLogsPorTopico(@PathVariable String topico) {
        return ResponseEntity.ok(auditoriaRepository.findByTopicoKafkaOrderByTimestampDesc(topico));
    }
}
