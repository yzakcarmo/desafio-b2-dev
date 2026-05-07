package com.yzakcarmo.desafiob2dev.strategy.validation;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.strategy.OrderStrategyContext;
import com.yzakcarmo.desafiob2dev.strategy.ValidationResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FarmaPremiumValidationStrategyTest {

    private final FarmaPremiumValidationStrategy strategy = new FarmaPremiumValidationStrategy();

    private OrderStrategyContext ctx(BigDecimal subtotal, int totalItems, OrderOrigin origin) {
        return new OrderStrategyContext("FARMA-PREMIUM", subtotal, totalItems, 1, origin, List.of());
    }

    @Test
    void valid_order_passes() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("1.00"), 1, OrderOrigin.API));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void no_minimum_value_enforced() {
        // Qualquer valor, mesmo zero, deve passar (sem mínimo para FARMA-PREMIUM)
        ValidationResult result = strategy.validate(ctx(BigDecimal.ZERO, 1, OrderOrigin.API));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void exactly_max_items_passes() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("100.00"), 500, OrderOrigin.API));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void exceeds_max_items_fails() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("100.00"), 501, OrderOrigin.API));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().getFirst()).contains("500");
    }

    @Test
    void bonus_order_passes_without_restrictions() {
        ValidationResult result = strategy.validate(ctx(BigDecimal.ZERO, 1, OrderOrigin.BONUS));

        assertThat(result.isValid()).isTrue();
    }
}
