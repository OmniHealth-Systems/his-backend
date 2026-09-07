package com.clinic.farmacia_service.controller;

import com.clinic.farmacia_service.domain.DetalleVentaFarmacia;
import com.clinic.farmacia_service.domain.VentaFarmacia;
import com.clinic.farmacia_service.service.VentaFarmaciaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/farmacia/pos")
@RequiredArgsConstructor
public class VentaFarmaciaController {

    private final VentaFarmaciaService ventaService;

    @GetMapping("/ventas")
    public ResponseEntity<List<VentaFarmacia>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/ventas/{id}")
    public ResponseEntity<VentaFarmacia> obtenerVenta(@PathVariable String id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    @PostMapping("/ventas")
    public ResponseEntity<VentaFarmacia> procesarVentaDirecta(@RequestBody VentaFarmacia venta) {
        List<DetalleVentaFarmacia> detalles = venta.getDetalles();
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.procesarVentaPOS(venta, detalles));
    }
}
