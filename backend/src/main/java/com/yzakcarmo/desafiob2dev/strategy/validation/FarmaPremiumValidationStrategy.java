package com.yzakcarmo.desafiob2dev.strategy.validation;

import com.yzakcarmo.desafiob2dev.strategy.*;

import java.util.ArrayList;
import java.util.List;

@TenantStrategy(tenantCode = "FARMA-PREMIUM")
public class FarmaPremiumValidationStrategy implements OrderValidationStrategy {

    private static final int MAX_ITEMS = 500;

    @Override
    public ValidationResult validate(OrderStrategyContext context) {
        List<String> errors = new ArrayList<>();

        if (context.getTotalItems() > MAX_ITEMS) {
            errors.add("Máximo de " + MAX_ITEMS + " itens por pedido. Quantidade atual: " + context.getTotalItems());
        }

        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.failure(errors);
    }
}