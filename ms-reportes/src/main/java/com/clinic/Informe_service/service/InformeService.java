package com.clinic.Informe_service.service;

import com.clinic.Informe_service.DTO.InformeCompletoDTO;
import com.clinic.Informe_service.DTO.InformeDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface InformeService {
    InformeDTO crearInforme(InformeDTO informeDTO);
    InformeDTO actualizarInforme(Long id, InformeDTO informeDTO);
    InformeDTO obtenerInformePorId(Long id);
    InformeCompletoDTO obtenerInformeCompletoPorId(Long id);
    ResponseEntity<Resource> generarPdf(Long id);
    void enviarPorEmail(Long id, String destinatario);
}
