package com.yzakcarmo.desafiob2dev.api.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
        UUID orderId,
        String externalReference,
        String status,
        String origin,
        BigDecimal subtotal,
        BigDecimal discountValue,
        BigDecimal total,
        OffsetDateTime createdAt,
        OffsetDateTime lastModified,
        BuyerInfo buyer,
        SellerInfo seller,
        WarehouseInfo warehouse,
        PaymentConditionInfo paymentCondition,
        List<ItemInfo> items
) {
    public record BuyerInfo(String externalReference, String name) {}
    public record SellerInfo(String externalReference, String name) {}
    public record WarehouseInfo(String externalReference, String name) {}
    public record PaymentConditionInfo(String code, String description, int maxInstallments) {}
    public record ItemInfo(
            String productCode,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal listPrice,
            BigDecimal subtotal
    ) {}
}