package com.clinic.Informe_service.controller;

import com.clinic.Informe_service.DTO.InformeDTO;
import com.clinic.Informe_service.service.InformeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/informes")
@RequiredArgsConstructor
public class InformeController {

    private final InformeService informeService;

    @PostMapping
    public ResponseEntity<InformeDTO> crearInforme(@RequestBody InformeDTO informeDTO) {
        InformeDTO nuevoInforme = informeService.crearInforme(informeDTO);
        return new ResponseEntity<>(nuevoInforme, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InformeDTO> actualizarInforme(
            @PathVariable Long id,
            @RequestBody InformeDTO informeDTO) {
        InformeDTO actualizado = informeService.actualizarInforme(id, informeDTO);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InformeDTO> obtenerInformePorId(@PathVariable Long id) {
        InformeDTO informe = informeService.obtenerInformePorId(id);
        return ResponseEntity.ok(informe);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> descargarPdf(@PathVariable Long id) {
        return informeService.generarPdf(id);
    }

    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<Void> enviarInformePorEmail(
            @PathVariable Long id,
            @RequestParam String destinatario) {

        informeService.enviarPorEmail(id, destinatario);
        return ResponseEntity.ok().build();
    }
}