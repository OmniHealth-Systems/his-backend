package com.clinic.Franquicia_service.Controller;
import com.clinic.Franquicia_service.domain.Clinica;
import com.clinic.Franquicia_service.domain.Sede;
import com.clinic.Franquicia_service.service.ClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping(value = {"/api/v1/clinicas", "/api/clinicas"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClinicaController {
    private final ClinicaService clinicaService;

    @GetMapping
    public ResponseEntity<List<Clinica>> getAllClinicas() {
        return ResponseEntity.ok(clinicaService.findAllClinicas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clinica> getClinicaById(@PathVariable Long id) {
        Optional<Clinica> clinica = clinicaService.findClinicaById(id);
        return clinica.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Clinica> createClinica(@RequestBody Clinica clinica) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicaService.saveClinica(clinica));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinica(@PathVariable Long id) {
        clinicaService.deleteClinica(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clinicaId}/sedes")
    public ResponseEntity<List<Sede>> getSedesByClinica(@PathVariable Long clinicaId) {
        return ResponseEntity.ok(clinicaService.findSedesByClinicaId(clinicaId));
    }

    @GetMapping("/sedes/{sedeId}/disponible")
    public ResponseEntity<Boolean> checkSedeDisponible(@PathVariable Long sedeId) {
        return ResponseEntity.ok(clinicaService.isSedeDisponible(sedeId));
    }
}
