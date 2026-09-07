package com.clinic.Informe_service.client;

import com.clinic.Informe_service.DTO.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// PersonalClient.java
@FeignClient(name = "personal-service", url = "${feign.client.config.personal-service.url}")
public interface PersonalClient {

    @GetMapping("/doctores/{id}")
    DoctorDTO getDoctorById(@PathVariable Long id);
}