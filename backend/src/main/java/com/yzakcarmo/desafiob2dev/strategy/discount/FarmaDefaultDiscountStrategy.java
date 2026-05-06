package com.yzakcarmo.desafiob2dev.strategy.discount;

import com.yzakcarmo.desafiob2dev.strategy.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@TenantStrategy(tenantCode = "FARMA-DEFAULT")
public class FarmaDefaultDiscountStrategy implements OrderDiscountStrategy {

    private static final BigDecimal DISCOUNT_THRESHOLD    = new BigDecimal("500.00");
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("1000.00");
    private static final BigDecimal DISCOUNT_RATE         = new BigDecimal("0.05");

    @Override
    public DiscountResult calculate(OrderStrategyContext context, PricingResult pricing) {
        BigDecimal subtotal = pricing.getSubtotal();

        if (subtotal.compareTo(DISCOUNT_THRESHOLD) >= 0) {
            BigDecimal discountValue = subtotal.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
            boolean freeShipping = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0;
            return new DiscountResult(discountValue, new BigDecimal("5.00"),
                    "Desconto padrão 5% para pedidos acima de R$ 500,00", freeShipping);
        }

        return DiscountResult.noDiscount();
    }
}