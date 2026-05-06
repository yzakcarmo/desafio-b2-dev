package com.yzakcarmo.desafiob2dev.strategy;

public interface OrderPricingStrategy {
    PricingResult calculate(OrderStrategyContext context);
}