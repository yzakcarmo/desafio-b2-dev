package com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderEventPayload {
    private UUID orderId;
    private String externalReference;
    private BigDecimal total;
    private String status;
    private String tenant;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}

