package com.yzakcarmo.desafiob2dev.strategy.discount;

import com.yzakcarmo.desafiob2dev.strategy.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@TenantStrategy(tenantCode = "FARMA-ECONOMIA")
public class FarmaEconomiaDiscountStrategy implements OrderDiscountStrategy {

    private static final BigDecimal VISTA_DISCOUNT_RATE = new BigDecimal("0.02");

    @Override
    public DiscountResult calculate(OrderStrategyContext context, PricingResult pricing) {
        if (context.maxInstallments() == 1) {
            BigDecimal subtotal = pricing.getSubtotal();
            BigDecimal discountValue = subtotal.multiply(VISTA_DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
            return new DiscountResult(discountValue, new BigDecimal("2.00"),
                    "Desconto 2% pagamento à vista", false);
        }

        return DiscountResult.noDiscount();
    }
}