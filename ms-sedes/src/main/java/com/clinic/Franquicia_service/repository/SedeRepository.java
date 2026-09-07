package com.clinic.Franquicia_service.repository;
import com.clinic.Franquicia_service.domain.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findByClinicaId(Long clinicaId);
    List<Sede> findByEstado(String estado);
}
