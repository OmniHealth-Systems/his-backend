package com.clinic.Pagos_Service.repository;

import com.clinic.Pagos_Service.domain.CoberturaSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoberturaSeguroRepository extends JpaRepository<CoberturaSeguro, Long> {
    List<CoberturaSeguro> findByPacienteId(Long pacienteId);
    Optional<CoberturaSeguro> findByNumeroPoliza(String numeroPoliza);
    List<CoberturaSeguro> findByAseguradora(String aseguradora);
}
