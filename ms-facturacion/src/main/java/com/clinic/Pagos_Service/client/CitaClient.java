package com.clinic.Pagos_Service.client;

import com.clinic.Pagos_Service.DTO.CitaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.UUID;

@FeignClient(name = "citas-service", url = "${feign.client.config.citas-service.url}")
public interface CitaClient {
    @GetMapping("/api/citas/{id}")
    CitaDTO getCitaById(@PathVariable UUID id);

    @PutMapping("/api/citas/{id}/estado")
    void actualizarEstadoCita(
            @PathVariable UUID id,
            @RequestBody String estado
    );
}