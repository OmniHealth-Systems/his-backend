package com.clinic.empleados_service.repository;

import com.clinic.empleados_service.domain.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByFecha(LocalDate fecha);
    List<Turno> findByEstado(Turno.EstadoTurno estado);
}