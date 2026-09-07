package com.clinic.notificacionesservice.repository;

import com.clinic.notificacionesservice.domain.Canal;
import com.clinic.notificacionesservice.domain.enums.TipoCanal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// repository/CanalRepository.java
public interface CanalRepository extends JpaRepository<Canal, Long> {
    Optional<Canal> findByTipo(TipoCanal tipo);
}