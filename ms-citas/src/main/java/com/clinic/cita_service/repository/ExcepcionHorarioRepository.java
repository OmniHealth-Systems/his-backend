package com.clinic.cita_service.repository;

import com.clinic.cita_service.domain.ExcepcionHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExcepcionHorarioRepository extends JpaRepository<ExcepcionHorario, Long> {
    List<ExcepcionHorario> findByDoctorIdAndFecha(Long doctorId, LocalDate fecha);

    List<ExcepcionHorario> findByFranquiciaIdAndFecha(Long franquiciaId, LocalDate fecha);
}