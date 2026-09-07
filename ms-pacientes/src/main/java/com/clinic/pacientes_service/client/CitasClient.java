package com.clinic.pacientes_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas-service", url = "${feign.client.config.citas-service.url}")
public interface CitasClient {

    @GetMapping("/api/citas/paciente/{pacienteId}/existe")
    @CircuitBreaker(name = "citasService", fallbackMethod = "checkPacienteTieneCitasFallback")
    boolean checkPacienteTieneCitas(@PathVariable Long pacienteId);

    default boolean checkPacienteTieneCitasFallback(Long pacienteId, Throwable throwable) {
        // En caso de fallo, asumir que el paciente tiene citas para evitar eliminaciones
        return true;
    }
}