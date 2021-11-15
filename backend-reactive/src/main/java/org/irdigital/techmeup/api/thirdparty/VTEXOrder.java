package org.irdigital.techmeup.api.thirdparty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class VTEXOrder {
    private String orderId;
    private String creationDate;
    private String clientName;
    private String items;
    private BigDecimal totalValue;
    private String paymentNames;
    private String status;
    private String statusDescription;
    private String marketPlaceOrderId;
    private String sequence;
    private String salesChannel;
    private String affiliateId;
    private String origin;
    private String workflowInErrorState;
    private String workflowInRetry;
    private String lastMessageUnread;
    @JsonProperty("ShippingEstimatedDate")
    private String ShippingEstimatedDate;
    @JsonProperty("ShippingEstimatedDateMax")
    private String ShippingEstimatedDateMax;
    @JsonProperty("ShippingEstimatedDateMin")
    private String ShippingEstimatedDateMin;
    private String orderIsComplete;
    private String listId;
    private String listType;
    private String authorizedDate;
    private String callCenterOperatorName;
    private Integer totalItems;
    private String currencyCode;
    private String hostname;
    private List<String> invoiceOutput;
    private List<String> invoiceInput;
    private String lastChange;
    private String isAllDelivered;
    private String isAnyDelivered;
    private String giftCardProviders;
    private String orderFormId;
    private String paymentApprovedDate;
    private String readyForHandlingDate;
}
