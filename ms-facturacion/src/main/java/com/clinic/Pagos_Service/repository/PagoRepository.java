package com.clinic.Pagos_Service.repository;

import com.clinic.Pagos_Service.domain.EstadoPago;
import com.clinic.Pagos_Service.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {
    List<Pago> findByCitaId(UUID citaId);
    List<Pago> findByPacienteId(UUID pacienteId);
    List<Pago> findByEstado(EstadoPago estado);
    // Nuevos métodos agregados
    List<Pago> findByFechaCreacionBetween(LocalDateTime start, LocalDateTime end);
    List<Pago> findByPacienteIdAndFechaCreacionAfter(UUID pacienteId, LocalDateTime fecha);
    Optional<Pago> findByReferenciaPago(String referenciaPago);
}
