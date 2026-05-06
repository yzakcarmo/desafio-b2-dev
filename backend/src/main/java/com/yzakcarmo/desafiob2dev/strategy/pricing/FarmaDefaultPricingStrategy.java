package com.yzakcarmo.desafiob2dev.strategy.pricing;

import com.yzakcarmo.desafiob2dev.strategy.*;

import java.math.BigDecimal;

@TenantStrategy(tenantCode = "FARMA-DEFAULT")
public class FarmaDefaultPricingStrategy implements OrderPricingStrategy {

    @Override
    public PricingResult calculate(OrderStrategyContext context) {
        BigDecimal subtotal = context.getItems().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PricingResult(subtotal, "Precificação padrão aplicada");
    }
}