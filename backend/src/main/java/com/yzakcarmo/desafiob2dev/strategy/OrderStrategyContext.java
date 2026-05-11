package com.yzakcarmo.desafiob2dev.strategy;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record OrderStrategyContext(String tenantCode, BigDecimal rawSubtotal, int totalItems, int maxInstallments,
                                   OrderOrigin origin, List<ItemContext> items) {

    @Override
    public BigDecimal rawSubtotal() {
        return rawSubtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public record ItemContext(String productCode, int quantity, BigDecimal unitPrice, BigDecimal listPrice) {
    }
}