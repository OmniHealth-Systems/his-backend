package com.clinic.laboratorio_service.repository;

import com.clinic.laboratorio_service.domain.OrdenLaboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenLaboratorioRepository extends JpaRepository<OrdenLaboratorio, Long> {
    List<OrdenLaboratorio> findByPacienteIdOrderByFechaOrdenDesc(Long pacienteId);
    List<OrdenLaboratorio> findByDoctorIdOrderByFechaOrdenDesc(Long doctorId);
    List<OrdenLaboratorio> findByEstadoOrderByFechaOrdenDesc(OrdenLaboratorio.EstadoOrden estado);
    List<OrdenLaboratorio> findByConsultaId(Long consultaId);
}
