package com.yzakcarmo.desafiob2dev.api.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateOrderResponse {

    private String code;
    private String message;
    private Data data;

    public CreateOrderResponse(String code, String message, Data data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public record Data(
            UUID orderId,
            String externalReference,
            String status,
            BigDecimal subtotal,
            BigDecimal discountValue,
            BigDecimal total,
            int itemCount,
            Validation validation,
            Pricing pricing,
            Discount discount
    ) {}

    public record Validation(List<String> warnings) {}

    public record Pricing(BigDecimal subtotal, String description) {}

    public record Discount(
            BigDecimal value,
            BigDecimal percentage,
            String description,
            boolean freeShipping
    ) {}
}