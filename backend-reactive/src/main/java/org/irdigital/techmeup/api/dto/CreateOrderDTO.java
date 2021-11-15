package org.irdigital.techmeup.api.dto;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private String orderId;
    private String currentState;
}
