package com.yzakcarmo.desafiob2dev.strategy.validation;

import com.yzakcarmo.desafiob2dev.strategy.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TenantStrategy(tenantCode = "FARMA-DEFAULT")
public class FarmaDefaultValidationStrategy implements OrderValidationStrategy {

    private static final BigDecimal MIN_ORDER_VALUE = new BigDecimal("50.00");
    private static final int MAX_ITEMS = 100;

    @Override
    public ValidationResult validate(OrderStrategyContext context) {
        List<String> errors = new ArrayList<>();

        if (context.rawSubtotal().compareTo(MIN_ORDER_VALUE) < 0) {
            errors.add("Pedido mínimo de R$ 50,00. Valor atual: R$ " + context.rawSubtotal());
        }

        if (context.totalItems() > MAX_ITEMS) {
            errors.add("Máximo de " + MAX_ITEMS + " itens por pedido. Quantidade atual: " + context.totalItems());
        }

        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.failure(errors);
    }
}