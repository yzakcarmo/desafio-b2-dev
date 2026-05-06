package com.yzakcarmo.desafiob2dev.strategy;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;

import java.math.BigDecimal;
import java.util.List;

public class OrderStrategyContext {

    private final String tenantCode;
    private final BigDecimal rawSubtotal;
    private final int totalItems;
    private final int maxInstallments;
    private final OrderOrigin origin;
    private final List<ItemContext> items;

    public OrderStrategyContext(String tenantCode, BigDecimal rawSubtotal, int totalItems,
                                int maxInstallments, OrderOrigin origin, List<ItemContext> items) {
        this.tenantCode = tenantCode;
        this.rawSubtotal = rawSubtotal;
        this.totalItems = totalItems;
        this.maxInstallments = maxInstallments;
        this.origin = origin;
        this.items = items;
    }

    public String getTenantCode() { return tenantCode; }
    public BigDecimal getRawSubtotal() { return rawSubtotal; }
    public int getTotalItems() { return totalItems; }
    public int getMaxInstallments() { return maxInstallments; }
    public OrderOrigin getOrigin() { return origin; }
    public List<ItemContext> getItems() { return items; }

    public record ItemContext(String productCode, int quantity, BigDecimal unitPrice, BigDecimal listPrice) {}
}