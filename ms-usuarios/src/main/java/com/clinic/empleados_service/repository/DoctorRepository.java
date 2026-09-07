package com.clinic.empleados_service.repository;

import com.clinic.empleados_service.domain.Doctor;
import com.clinic.empleados_service.domain.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByCmp(String cmp);
    List<Doctor> findByEspecialidadId(Long especialidadId);
    List<Doctor> findByTurnoEstado(Turno.EstadoTurno estado);
}