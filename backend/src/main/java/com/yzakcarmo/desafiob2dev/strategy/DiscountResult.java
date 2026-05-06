package com.yzakcarmo.desafiob2dev.strategy;

import java.math.BigDecimal;

public class DiscountResult {

    private final BigDecimal discountValue;
    private final BigDecimal discountPercentage;
    private final String description;
    private final boolean freeShipping;

    public DiscountResult(BigDecimal discountValue, BigDecimal discountPercentage,
                          String description, boolean freeShipping) {
        this.discountValue = discountValue;
        this.discountPercentage = discountPercentage;
        this.description = description;
        this.freeShipping = freeShipping;
    }

    public static DiscountResult noDiscount() {
        return new DiscountResult(BigDecimal.ZERO, BigDecimal.ZERO, "Sem desconto aplicado", false);
    }

    public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public String getDescription() { return description; }
    public boolean isFreeShipping() { return freeShipping; }
}