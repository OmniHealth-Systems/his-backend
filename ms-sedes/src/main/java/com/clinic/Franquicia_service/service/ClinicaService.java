package com.clinic.Franquicia_service.service;
import com.clinic.Franquicia_service.Client.CitasClient;
import com.clinic.Franquicia_service.domain.Clinica;
import com.clinic.Franquicia_service.domain.Sede;
import com.clinic.Franquicia_service.repository.ClinicaRepository;
import com.clinic.Franquicia_service.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClinicaService {
    private final ClinicaRepository clinicaRepository;
    private final SedeRepository sedeRepository;
    private final CitasClient citasClient;

    public List<Clinica> findAllClinicas() {
        return clinicaRepository.findAll();
    }

    public Optional<Clinica> findClinicaById(Long id) {
        return clinicaRepository.findById(id);
    }

    @Transactional
    public Clinica saveClinica(Clinica clinica) {
        return clinicaRepository.save(clinica);
    }

    @Transactional
    public void deleteClinica(Long id) {
        clinicaRepository.deleteById(id);
    }

    public List<Sede> findSedesByClinicaId(Long clinicaId) {
        return sedeRepository.findByClinicaId(clinicaId);
    }

    public boolean isSedeDisponible(Long sedeId) {
        return citasClient.checkSedeDisponible(sedeId);
    }
}
