package com.clinic.pacientes_service.controller;

import com.clinic.pacientes_service.DTO.DireccionRequestDTO;
import com.clinic.pacientes_service.domain.Direccion;
import com.clinic.pacientes_service.service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/direcciones")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @PostMapping
    public ResponseEntity<Direccion> crearDireccion(@RequestBody DireccionRequestDTO dto) {
        Direccion direccion = direccionService.crearDireccion(dto);
        return ResponseEntity.ok(direccion);
    }
}