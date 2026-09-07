package com.clinic.soporte_service.repository;

import com.clinic.soporte_service.domain.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByActivoTrueOrderByOrdenAsc();
    List<FAQ> findByCategoriaAndActivoTrue(String categoria);
}
