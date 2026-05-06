package com.yzakcarmo.desafiob2dev.strategy;

public interface OrderValidationStrategy {
    ValidationResult validate(OrderStrategyContext context);
}