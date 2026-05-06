package com.yzakcarmo.desafiob2dev.strategy;

import java.math.BigDecimal;

public class PricingResult {

    private final BigDecimal subtotal;
    private final String description;

    public PricingResult(BigDecimal subtotal, String description) {
        this.subtotal = subtotal;
        this.description = description;
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public String getDescription() { return description; }
}