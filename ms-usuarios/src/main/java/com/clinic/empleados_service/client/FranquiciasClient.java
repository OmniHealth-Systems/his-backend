package com.clinic.empleados_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "franquicias-service", url = "${feign.client.config.franquicias-service.url}")
public interface FranquiciasClient {

    @GetMapping("/api/sedes/{sedeId}/disponible")
    @CircuitBreaker(name = "franquiciasService", fallbackMethod = "checkSedeDisponibleFallback")
    boolean checkSedeDisponible(@PathVariable Long sedeId);

    default boolean checkSedeDisponibleFallback(Long sedeId, Throwable throwable) {
        // Fallback: asumir que la sede está disponible
        return true;
    }
}
