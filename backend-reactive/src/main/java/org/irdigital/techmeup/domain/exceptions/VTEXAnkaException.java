package org.irdigital.techmeup.domain.exceptions;

import lombok.Getter;
import pe.intercorpretail.anka.core.exception.model.AnkaErrorCode;
import pe.intercorpretail.anka.core.exception.model.AnkaException;

@Getter
public class VTEXAnkaException extends AnkaException {
    private final String tenantId;
    private final String orderId;

    public VTEXAnkaException(String tenantId, String orderId, String message) {
        super(AnkaErrorCode.FIL_VTX_001, message);
        this.tenantId = tenantId;
        this.orderId = orderId;
    }
}
