package com.clinic.turnos_service.controller;

import com.clinic.turnos_service.domain.AgendaMedica;
import com.clinic.turnos_service.domain.AusenciaMedico;
import com.clinic.turnos_service.repository.AgendaMedicaRepository;
import com.clinic.turnos_service.repository.AusenciaMedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaMedicaRepository agendaRepository;
    private final AusenciaMedicoRepository ausenciaRepository;

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AgendaMedica>> obtenerAgendasPorDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(agendaRepository.findByDoctorIdAndActivoTrue(doctorId));
    }

    @PostMapping
    public ResponseEntity<AgendaMedica> crearAgenda(@RequestBody AgendaMedica agenda) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaRepository.save(agenda));
    }

    @GetMapping("/ausencias/doctor/{doctorId}")
    public ResponseEntity<List<AusenciaMedico>> obtenerAusencias(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ausenciaRepository.findByDoctorIdAndEstado(doctorId, AusenciaMedico.EstadoAusencia.APROBADA));
    }

    @PostMapping("/ausencias")
    public ResponseEntity<AusenciaMedico> registrarAusencia(@RequestBody AusenciaMedico ausencia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ausenciaRepository.save(ausencia));
    }
}
