package com.clinic.Franquicia_service.Controller;
import com.clinic.Franquicia_service.domain.Sede;
import com.clinic.Franquicia_service.service.SedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/api/v1/sedes", "/api/sedes"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SedeController {
    private final SedeService sedeService;

    @GetMapping
    public ResponseEntity<List<Sede>> getAllSedes() {
        return ResponseEntity.ok(sedeService.findAllSedes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sede> getSedeById(@PathVariable Long id) {
        Optional<Sede> sede = sedeService.findSedeById(id);
        return sede.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Sede> createSede(@RequestBody Sede sede) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sedeService.saveSede(sede));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sede> updateSede(@PathVariable Long id, @RequestBody Sede sede) {
        sede.setId(id);
        return ResponseEntity.ok(sedeService.saveSede(sede));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSede(@PathVariable Long id) {
        sedeService.deleteSede(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Sede>> getSedesActivas() {
        return ResponseEntity.ok(sedeService.findActiveSedes());
    }
}