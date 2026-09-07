package com.clinic.documentos_service.controller;

import com.clinic.documentos_service.domain.Documento;
import com.clinic.documentos_service.repository.DocumentoRepository;
import com.clinic.documentos_service.service.AlmacenamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final AlmacenamientoService almacenamientoService;
    private final DocumentoRepository documentoRepository;

    @PostMapping("/subir")
    public ResponseEntity<Documento> subirDocumento(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") Documento.TipoDocumento tipo,
            @RequestParam("entidadOrigen") String entidadOrigen,
            @RequestParam("entidadId") String entidadId,
            @RequestParam(value = "pacienteId", required = false) Long pacienteId
    ) throws IOException {
        Documento doc = almacenamientoService.subirDocumento(file, tipo, entidadOrigen, entidadId, pacienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documento> obtenerPorId(@PathVariable String id) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Documento no encontrado con ID: " + id));
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/{id}/url-descarga")
    public ResponseEntity<Map<String, String>> obtenerUrlDescarga(@PathVariable String id) {
        String presignedUrl = almacenamientoService.generarPresignedUrl(id);
        return ResponseEntity.ok(Map.of("url", presignedUrl));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Documento>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(documentoRepository.findByPacienteId(pacienteId));
    }
}
