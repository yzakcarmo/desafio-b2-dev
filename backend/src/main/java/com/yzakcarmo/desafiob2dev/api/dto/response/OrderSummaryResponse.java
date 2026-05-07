package com.yzakcarmo.desafiob2dev.api.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderSummaryResponse(
        UUID orderId,
        String externalReference,
        String buyerName,
        String sellerName,
        String warehouseName,
        String status,
        BigDecimal subtotal,
        BigDecimal discountValue,
        BigDecimal total,
        int itemCount,
        String origin,
        OffsetDateTime createdAt
) {}