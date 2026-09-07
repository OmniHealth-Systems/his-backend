package com.clinic.pacientes_service.service;

import com.clinic.pacientes_service.DTO.DireccionRequestDTO;
import com.clinic.pacientes_service.domain.Direccion;
import com.clinic.pacientes_service.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionService {
    @Autowired
    private DireccionRepository direccionRepository;

    public Direccion crearDireccion(DireccionRequestDTO dto) {
        Direccion direccion = Direccion.builder()
                .departamento(dto.getDepartamento())
                .provincia(dto.getProvincia())
                .ciudad(dto.getCiudad())
                .build();
        return direccionRepository.save(direccion);
    }
}
