package com.clinic.Pagos_Service.repository;

import com.clinic.Pagos_Service.domain.ComprobantePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, String> {
    Optional<ComprobantePago> findByNumeroComprobante(String numeroComprobante);
    List<ComprobantePago> findByPacienteIdOrderByFechaEmisionDesc(Long pacienteId);
    List<ComprobantePago> findByEstadoOrderByFechaEmisionDesc(ComprobantePago.EstadoComprobante estado);
}
