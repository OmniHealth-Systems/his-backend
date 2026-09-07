package com.clinic.Pagos_Service.repository;

import com.clinic.Pagos_Service.domain.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {
    List<DetalleFactura> findByComprobanteId(Long comprobanteId);
}
