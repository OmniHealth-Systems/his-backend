package com.clinic.cita_service.Controller;
import com.clinic.cita_service.DTO.CitaRequestDTO;
import com.clinic.cita_service.DTO.CitaResponseDTO;
import com.clinic.cita_service.service.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/api/v1/citas", "/api/citas"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CitaController {
    private final CitaService citaService;

    @PostMapping
    public ResponseEntity<CitaResponseDTO> crearCita(@Validated @RequestBody CitaRequestDTO citaRequest) {
        CitaResponseDTO nuevaCita = citaService.crearCita(citaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCita);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> obtenerCita(@PathVariable Long id) {
        CitaResponseDTO cita = citaService.obtenerCitaPorId(id);
        return ResponseEntity.ok(cita);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasPorPaciente(
            @PathVariable Long pacienteId) {
        List<CitaResponseDTO> citas = citaService.obtenerCitasPorPaciente(pacienteId);
        return ResponseEntity.ok(citas);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id) {
        citaService.cancelarCita(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibilidad/doctor/{doctorId}/fecha/{fecha}")
    public ResponseEntity<List<String>> obtenerHorariosDisponibles(
            @PathVariable Long doctorId,
            @PathVariable String fecha) {
        // Implementación para obtener horarios disponibles
        return ResponseEntity.ok(List.of("09:00", "10:30", "14:00"));
    }
}
