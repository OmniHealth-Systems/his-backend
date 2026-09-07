package com.clinic.Pagos_Service.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReembolsoRequest {
    private BigDecimal montoReembolso;
    private String motivo;
}