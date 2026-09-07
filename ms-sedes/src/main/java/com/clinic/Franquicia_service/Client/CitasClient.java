package com.clinic.Franquicia_service.Client;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas-service", url = "${feign.client.config.citas-service.url}")
public interface CitasClient {

    @GetMapping("/api/citas/sedes/{sedeId}/disponible")
    @CircuitBreaker(name = "citasService", fallbackMethod = "checkDisponibilidadFallback")
    boolean checkSedeDisponible(@PathVariable Long sedeId);

    default boolean checkDisponibilidadFallback(Long sedeId, Throwable throwable) {
        // Lógica de fallback: considerar como disponible para no bloquear operaciones
        return true;
    }
}