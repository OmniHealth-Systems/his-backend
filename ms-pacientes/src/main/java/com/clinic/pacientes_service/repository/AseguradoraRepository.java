package com.clinic.pacientes_service.repository;

import com.clinic.pacientes_service.domain.Aseguradora;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AseguradoraRepository extends JpaRepository<Aseguradora, Long> {
}