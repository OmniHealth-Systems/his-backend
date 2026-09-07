package com.clinic.Franquicia_service.service;
import com.clinic.Franquicia_service.domain.Sede;
import com.clinic.Franquicia_service.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SedeService {
    private final SedeRepository sedeRepository;

    public List<Sede> findAllSedes() {
        return sedeRepository.findAll();
    }

    public Optional<Sede> findSedeById(Long id) {
        return sedeRepository.findById(id);
    }

    @Transactional
    public Sede saveSede(Sede sede) {
        return sedeRepository.save(sede);
    }

    @Transactional
    public void deleteSede(Long id) {
        sedeRepository.deleteById(id);
    }

    public List<Sede> findActiveSedes() {
        return sedeRepository.findByEstado("ACTIVO");
    }
}