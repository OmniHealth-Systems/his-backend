package com.clinic.soporte_service.repository;

import com.clinic.soporte_service.domain.ComentarioTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioTicketRepository extends JpaRepository<ComentarioTicket, String> {
    List<ComentarioTicket> findByTicketId(String ticketId);
}
