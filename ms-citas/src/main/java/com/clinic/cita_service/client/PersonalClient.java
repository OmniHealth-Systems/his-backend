package com.clinic.cita_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios", url = "${feign.client.config.ms-usuarios.url:http://localhost:8084}")
public interface PersonalClient {

    @GetMapping("/api/v1/doctores/{doctorId}/disponible")
    @CircuitBreaker(name = "personalService", fallbackMethod = "checkDoctorDisponibleFallback")
    boolean checkDoctorDisponible(@PathVariable("doctorId") Long doctorId);

    @GetMapping("/api/v1/doctores/{doctorId}/especialidad")
    @CircuitBreaker(name = "personalService", fallbackMethod = "getEspecialidadDoctorFallback")
    Long getEspecialidadDoctor(@PathVariable("doctorId") Long doctorId);

    default boolean checkDoctorDisponibleFallback(Long doctorId, Throwable throwable) {
        // Fallback: considerar como disponible en contingencia
        return true;
    }

    default Long getEspecialidadDoctorFallback(Long doctorId, Throwable throwable) {
        return null;
    }
}