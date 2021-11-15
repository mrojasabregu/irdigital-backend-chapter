package org.irdigital.techmeup.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.irdigital.techmeup.api.thirdparty.VTEXOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("orders")
public class Order implements Serializable, Persistable<String> {
    @Id
    private String orderUUID;
    private String tenantId;
    private String orderId;
    private String sequence;
    private LocalDateTime creationDate;
    private String clientName;
    private BigDecimal totalValue;
    private LocalDateTime shippingEstimatedDateMax;
    private LocalDateTime shippingEstimatedDateMin;
    private LocalDateTime authorizedDate;
    private String currencyCode;
    private String hostname;

    public static String createId(String tenantId, String orderId) {
        String templatedOrderId = String.format("%s_%s", tenantId, orderId);
        return DigestUtils.md5DigestAsHex(templatedOrderId.getBytes());
    }

    public static Order createFromVtex(String tenantId, VTEXOrder vtexOrder) {
        return Order.builder()
                .orderUUID(createId(tenantId, vtexOrder.getOrderId()))
                .orderId(vtexOrder.getOrderId())
                .tenantId(tenantId)
                .sequence(vtexOrder.getSequence())
                .creationDate(parse(vtexOrder.getCreationDate()))
                .clientName(vtexOrder.getClientName())
                .totalValue(vtexOrder.getTotalValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING))
                .shippingEstimatedDateMax(parse(vtexOrder.getShippingEstimatedDateMax()))
                .shippingEstimatedDateMin(parse(vtexOrder.getShippingEstimatedDateMin()))
                .authorizedDate(parse(vtexOrder.getAuthorizedDate()))
                .currencyCode(vtexOrder.getCurrencyCode())
                .hostname(vtexOrder.getHostname())
                .build();
    }
    public static LocalDateTime parse(String dateTime) {
        if(dateTime != null) {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    }

    @Override
    public String getId() {
        return orderUUID;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
