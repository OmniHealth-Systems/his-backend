package com.clinic.HistorialClinica_service.controller;

import com.clinic.HistorialClinica_service.DTO.NuevoRegistroDTO;
import com.clinic.HistorialClinica_service.DTO.RegistroMedicoDTO;
import com.clinic.HistorialClinica_service.service.RegistroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registros")
@RequiredArgsConstructor
public class RegistroController {
    private final RegistroService registroService;

    @PostMapping
    public ResponseEntity<RegistroMedicoDTO> crearRegistro(
            @Valid @RequestBody NuevoRegistroDTO dto) {
        return new ResponseEntity<>(
                registroService.crearRegistro(dto),
                HttpStatus.CREATED);
    }
}