package com.clinic.soporte_service.controller;

import com.clinic.soporte_service.domain.ComentarioTicket;
import com.clinic.soporte_service.domain.FAQ;
import com.clinic.soporte_service.domain.TicketSoporte;
import com.clinic.soporte_service.service.SoporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/soporte")
@RequiredArgsConstructor
public class SoporteController {

    private final SoporteService soporteService;

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketSoporte>> listarTickets() {
        return ResponseEntity.ok(soporteService.listarTickets());
    }

    @GetMapping("/tickets/solicitante/{solicitanteId}")
    public ResponseEntity<List<TicketSoporte>> listarPorSolicitante(@PathVariable Long solicitanteId) {
        return ResponseEntity.ok(soporteService.listarPorSolicitante(solicitanteId));
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketSoporte> obtenerTicket(@PathVariable String id) {
        return ResponseEntity.ok(soporteService.obtenerPorId(id));
    }

    @PostMapping("/tickets")
    public ResponseEntity<TicketSoporte> crearTicket(@RequestBody TicketSoporte ticket) {
        return ResponseEntity.status(HttpStatus.CREATED).body(soporteService.crearTicket(ticket));
    }

    @PatchMapping("/tickets/{id}/estado")
    public ResponseEntity<TicketSoporte> cambiarEstado(
            @PathVariable String id,
            @RequestParam TicketSoporte.EstadoTicket nuevoEstado
    ) {
        return ResponseEntity.ok(soporteService.cambiarEstado(id, nuevoEstado));
    }

    @PostMapping("/tickets/{id}/comentarios")
    public ResponseEntity<ComentarioTicket> agregarComentario(
            @PathVariable String id,
            @RequestBody ComentarioTicket comentario
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(soporteService.agregarComentario(id, comentario));
    }

    @GetMapping("/faq")
    public ResponseEntity<List<FAQ>> listarFaqs() {
        return ResponseEntity.ok(soporteService.listarFaqs());
    }
}
