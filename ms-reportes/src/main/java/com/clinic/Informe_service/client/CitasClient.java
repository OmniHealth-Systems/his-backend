package com.clinic.Informe_service.client;

import com.clinic.Informe_service.DTO.CitaDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cita-service", url = "${feign.client.config.cita-service.url}")
public interface CitasClient {
    @GetMapping("/citas/{id}")
    @CircuitBreaker(name = "citasService", fallbackMethod = "fallbackGetCita")
    @Retry(name = "citasService")
    CitaDTO getCitaById(@PathVariable Long id);

    default CitaDTO fallbackGetCita(Long id, Throwable throwable) {
        return new CitaDTO();
    }
}