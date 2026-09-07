package com.clinic.pacientes_service.service;
import com.clinic.pacientes_service.DTO.AseguradoraRequestDTO;
import com.clinic.pacientes_service.domain.Aseguradora;
import com.clinic.pacientes_service.repository.AseguradoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AseguradoraService {

    @Autowired
    private AseguradoraRepository aseguradoraRepository;

    // Método para crear una nueva aseguradora
    public Aseguradora crearAseguradora(AseguradoraRequestDTO dto) {
        Aseguradora aseguradora = Aseguradora.builder()
                .nombre(dto.getNombre())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .build();
        return aseguradoraRepository.save(aseguradora);
    }

    // Método para obtener todas las aseguradoras
    public List<Aseguradora> obtenerAseguradoras() {
        return aseguradoraRepository.findAll();
    }
}