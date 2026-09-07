package com.clinic.auditoria_service.repository;

import com.clinic.auditoria_service.domain.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {
    List<RegistroAuditoria> findByPacienteIdOrderByTimestampDesc(Long pacienteId);
    List<RegistroAuditoria> findByTopicoKafkaOrderByTimestampDesc(String topicoKafka);
    List<RegistroAuditoria> findTop100ByOrderByTimestampDesc();
}
