package com.clinic.Pagos_Service.repository;

import com.clinic.Pagos_Service.domain.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TransaccionRepository extends JpaRepository<Transaccion, UUID> {
    List<Transaccion> findByPagoId(UUID pagoId);
}