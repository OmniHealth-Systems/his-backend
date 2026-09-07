package com.clinic.cita_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pacientes", url = "${feign.client.config.ms-pacientes.url:http://localhost:8083}")
public interface PacientesClient {

    @GetMapping("/api/v1/pacientes/exists/{pacienteId}")
    @CircuitBreaker(name = "pacientesService", fallbackMethod = "checkPacienteExisteFallback")
    boolean checkPacienteExiste(@PathVariable("pacienteId") Long pacienteId);

    default boolean checkPacienteExisteFallback(Long pacienteId, Throwable throwable) {
        // En caso de fallo o fallback, devuelve true para no bloquear el flujo si el servicio se encuentra en degradación controlada
        return true;
    }
}