package com.clinic.empleados_service.repository;

import com.clinic.empleados_service.domain.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    Optional<Especialidad> findByCodigo(String codigo);
    Optional<Especialidad> findByNombre(String nombre);
}