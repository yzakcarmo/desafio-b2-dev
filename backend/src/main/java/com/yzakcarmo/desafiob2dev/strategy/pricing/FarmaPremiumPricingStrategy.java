package com.yzakcarmo.desafiob2dev.strategy.pricing;

import com.yzakcarmo.desafiob2dev.strategy.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@TenantStrategy(tenantCode = "FARMA-PREMIUM")
public class FarmaPremiumPricingStrategy implements OrderPricingStrategy {

    private static final BigDecimal OPERATIONAL_FEE = new BigDecimal("1.02");

    @Override
    public PricingResult calculate(OrderStrategyContext context) {
        BigDecimal rawSubtotal = context.getItems().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotal = rawSubtotal.multiply(OPERATIONAL_FEE).setScale(2, RoundingMode.HALF_UP);

        return new PricingResult(subtotal, "Precificação premium com 2% de taxa operacional aplicada");
    }
}