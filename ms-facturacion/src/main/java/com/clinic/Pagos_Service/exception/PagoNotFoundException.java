package com.clinic.Pagos_Service.exception;

import java.util.UUID;

public class PagoNotFoundException extends PagoException {
    public PagoNotFoundException(UUID id) {
        super("Pago no encontrado con ID: " + id);
    }
}