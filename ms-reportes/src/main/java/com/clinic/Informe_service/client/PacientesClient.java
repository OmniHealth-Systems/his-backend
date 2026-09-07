package com.clinic.Informe_service.client;

import com.clinic.Informe_service.DTO.PacienteDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// PacientesClient.java
@FeignClient(name = "pacientes-service", url = "${feign.client.config.personal-service.url}")
public interface PacientesClient {

    @GetMapping("/pacientes/{id}")
    PacienteDTO getPacienteById(@PathVariable Long id);
}