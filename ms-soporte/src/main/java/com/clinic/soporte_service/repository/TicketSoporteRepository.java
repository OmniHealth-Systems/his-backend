package com.clinic.soporte_service.repository;

import com.clinic.soporte_service.domain.TicketSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketSoporteRepository extends JpaRepository<TicketSoporte, String> {
    Optional<TicketSoporte> findByNumeroTicket(String numeroTicket);
    List<TicketSoporte> findBySolicitanteId(Long solicitanteId);
    List<TicketSoporte> findByEstado(TicketSoporte.EstadoTicket estado);
    List<TicketSoporte> findByPrioridad(TicketSoporte.PrioridadTicket prioridad);
    List<TicketSoporte> findBySedeOrigenId(Long sedeOrigenId);
}
