package com.clinic.Gateway_Service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> defaultFallback(HttpServletRequest request) {
        return buildFallbackResponse("default-service", request.getRequestURI());
    }

    @GetMapping("/{serviceName}")
    public ResponseEntity<Map<String, Object>> serviceFallback(@PathVariable String serviceName, HttpServletRequest request) {
        return buildFallbackResponse(serviceName, request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String serviceName, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Service Unavailable (Circuit Breaker)");
        body.put("message", "El microservicio [" + serviceName + "] no responde o se encuentra en degradación. Por favor intente más tarde.");
        body.put("path", path);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
