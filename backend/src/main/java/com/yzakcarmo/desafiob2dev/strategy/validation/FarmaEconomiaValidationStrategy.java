package com.yzakcarmo.desafiob2dev.strategy.validation;

import com.yzakcarmo.desafiob2dev.strategy.*;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@TenantStrategy(tenantCode = "FARMA-ECONOMIA")
public class FarmaEconomiaValidationStrategy implements OrderValidationStrategy {

    private static final BigDecimal MIN_ORDER_VALUE = new BigDecimal("200.00");
    private static final int MAX_ITEMS = 50;
    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");
    private static final int BUSINESS_HOUR_START = 8;
    private static final int BUSINESS_HOUR_END   = 18;

    @Override
    public ValidationResult validate(OrderStrategyContext context) {
        List<String> errors = new ArrayList<>();

        ZonedDateTime now = ZonedDateTime.now(BRAZIL_ZONE);
        int hour = now.getHour();
        if (hour < BUSINESS_HOUR_START || hour >= BUSINESS_HOUR_END) {
            errors.add("Pedidos apenas em horário comercial (08h-18h, horário de Brasília). " +
                    "Horário atual: " + hour + "h");
        }

        if (context.getRawSubtotal().compareTo(MIN_ORDER_VALUE) < 0) {
            errors.add("Pedido mínimo de R$ 200,00. Valor atual: R$ " + context.getRawSubtotal());
        }

        if (context.getTotalItems() > MAX_ITEMS) {
            errors.add("Máximo de " + MAX_ITEMS + " itens por pedido. Quantidade atual: " + context.getTotalItems());
        }

        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.failure(errors);
    }
}