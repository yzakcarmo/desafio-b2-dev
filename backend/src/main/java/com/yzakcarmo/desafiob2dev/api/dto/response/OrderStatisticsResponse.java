package com.yzakcarmo.desafiob2dev.api.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderStatisticsResponse(
        String tenant,
        Period period,
        long totalOrders,
        long confirmedOrders,
        long cancelledOrders,
        BigDecimal totalRevenue,
        BigDecimal averageOrderValue,
        List<TopBuyer> topBuyers,
        List<TopProduct> topProducts
) {
    public record Period(OffsetDateTime from, OffsetDateTime to) {}

    public record TopBuyer(
            String name,
            long orderCount,
            BigDecimal totalSpent
    ) {}

    public record TopProduct(
            String productCode,
            String productName,
            long totalQuantity
    ) {}
}