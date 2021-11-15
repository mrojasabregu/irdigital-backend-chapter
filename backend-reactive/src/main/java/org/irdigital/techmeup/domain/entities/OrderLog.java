package org.irdigital.techmeup.domain.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderLog {
    private String tenantId;
    private String orderId;
    private String logError;
    private LocalDateTime eventTimestamp;
}
