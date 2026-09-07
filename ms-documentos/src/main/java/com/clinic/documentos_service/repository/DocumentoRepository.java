package com.clinic.documentos_service.repository;

import com.clinic.documentos_service.domain.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, String> {
    List<Documento> findByPacienteId(Long pacienteId);
    List<Documento> findByEntidadOrigenAndEntidadId(String entidadOrigen, String entidadId);
    List<Documento> findByTipoDocumento(Documento.TipoDocumento tipo);
}
