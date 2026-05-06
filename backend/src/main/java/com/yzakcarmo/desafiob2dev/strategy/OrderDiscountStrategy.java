package com.yzakcarmo.desafiob2dev.strategy;

public interface OrderDiscountStrategy {
    DiscountResult calculate(OrderStrategyContext context, PricingResult pricing);
}