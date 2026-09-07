package com.clinic.Informe_service.repository;

import com.clinic.Informe_service.domain.Informe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InformeRepository extends JpaRepository<Informe, Long> {
}