package com.clinic.soporte_service.service;

import com.clinic.soporte_service.domain.ComentarioTicket;
import com.clinic.soporte_service.domain.FAQ;
import com.clinic.soporte_service.domain.TicketSoporte;
import com.clinic.soporte_service.repository.ComentarioTicketRepository;
import com.clinic.soporte_service.repository.FAQRepository;
import com.clinic.soporte_service.repository.TicketSoporteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoporteService {

    private final TicketSoporteRepository ticketRepository;
    private final ComentarioTicketRepository comentarioRepository;
    private final FAQRepository faqRepository;

    public List<TicketSoporte> listarTickets() {
        return ticketRepository.findAll();
    }

    public List<TicketSoporte> listarPorSolicitante(Long solicitanteId) {
        return ticketRepository.findBySolicitanteId(solicitanteId);
    }

    public TicketSoporte obtenerPorId(String id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con ID: " + id));
    }

    @Transactional
    public TicketSoporte crearTicket(TicketSoporte ticket) {
        String numTicket = "TCK-" + System.currentTimeMillis() % 1000000;
        ticket.setNumeroTicket(numTicket);
        if (ticket.getEstado() == null) {
            ticket.setEstado(TicketSoporte.EstadoTicket.ABIERTO);
        }
        if (ticket.getPrioridad() == null) {
            ticket.setPrioridad(TicketSoporte.PrioridadTicket.MEDIA);
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public TicketSoporte cambiarEstado(String ticketId, TicketSoporte.EstadoTicket nuevoEstado) {
        TicketSoporte ticket = obtenerPorId(ticketId);
        ticket.setEstado(nuevoEstado);
        if (nuevoEstado == TicketSoporte.EstadoTicket.RESUELTO || nuevoEstado == TicketSoporte.EstadoTicket.CERRADO) {
            ticket.setFechaResolucion(LocalDateTime.now());
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public ComentarioTicket agregarComentario(String ticketId, ComentarioTicket comentario) {
        TicketSoporte ticket = obtenerPorId(ticketId);
        comentario.setTicket(ticket);
        return comentarioRepository.save(comentario);
    }

    public List<FAQ> listarFaqs() {
        return faqRepository.findByActivoTrueOrderByOrdenAsc();
    }
}
