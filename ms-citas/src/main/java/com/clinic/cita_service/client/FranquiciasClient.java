package com.clinic.cita_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-sedes", url = "${feign.client.config.ms-sedes.url:http://localhost:8081}")
public interface FranquiciasClient {

    @GetMapping("/api/v1/clinicas/sedes/{sedeId}/disponible")
    @CircuitBreaker(name = "clinicasService", fallbackMethod = "checkSedeDisponibleFallback")
    boolean checkSedeDisponible(@PathVariable("sedeId") Long sedeId);

    default boolean checkSedeDisponibleFallback(Long sedeId, Throwable throwable) {
        return true;
    }
}