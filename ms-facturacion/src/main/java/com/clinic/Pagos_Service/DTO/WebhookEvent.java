package com.clinic.Pagos_Service.DTO;

import lombok.Data;
import java.util.Map;

@Data
public class WebhookEvent {
    private String id;
    private String type;
    private Map<String, Object> data;
    private Long created;
}