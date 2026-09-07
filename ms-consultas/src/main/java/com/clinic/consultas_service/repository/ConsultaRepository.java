package com.clinic.consultas_service.repository;

import com.clinic.consultas_service.domain.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId);
    List<Consulta> findByDoctorIdOrderByFechaConsultaDesc(Long doctorId);
    List<Consulta> findByCitaId(Long citaId);
}
