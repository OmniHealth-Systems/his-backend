package com.clinic.Pagos_Service.DTO;

import lombok.Data;
import java.util.Map;

@Data
public class ProcesarPagoRequest {
    private Map<String, Object> datosPago;
    private String tokenTarjeta; // Solo para pagos con tarjeta
    private String emailPayPal;  // Solo para PayPal
}