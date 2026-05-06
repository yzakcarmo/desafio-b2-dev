package com.yzakcarmo.desafiob2dev.strategy.discount;

import com.yzakcarmo.desafiob2dev.strategy.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@TenantStrategy(tenantCode = "FARMA-PREMIUM")
public class FarmaPremiumDiscountStrategy implements OrderDiscountStrategy {

    private static final BigDecimal BASE_DISCOUNT_RATE  = new BigDecimal("0.08");
    private static final BigDecimal EXTRA_DISCOUNT_RATE = new BigDecimal("0.03");

    @Override
    public DiscountResult calculate(OrderStrategyContext context, PricingResult pricing) {
        BigDecimal subtotal = pricing.getSubtotal();
        BigDecimal rate = BASE_DISCOUNT_RATE;
        String description = "Desconto premium 8%";

        if (context.getMaxInstallments() == 1) {
            rate = BASE_DISCOUNT_RATE.add(EXTRA_DISCOUNT_RATE);
            description = "Desconto premium 8% + 3% pagamento à vista";
        }

        BigDecimal discountValue = subtotal.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountPercentage = rate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

        return new DiscountResult(discountValue, discountPercentage, description, true);
    }
}