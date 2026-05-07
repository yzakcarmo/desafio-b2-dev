package com.yzakcarmo.desafiob2dev.strategy.validation;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.strategy.OrderStrategyContext;
import com.yzakcarmo.desafiob2dev.strategy.ValidationResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FarmaDefaultValidationStrategyTest {

    private final FarmaDefaultValidationStrategy strategy = new FarmaDefaultValidationStrategy();

    private OrderStrategyContext ctx(BigDecimal subtotal, int totalItems) {
        return new OrderStrategyContext("FARMA-DEFAULT", subtotal, totalItems, 1, OrderOrigin.API, List.of());
    }

    @Test
    void valid_order_passes() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("200.00"), 10));

        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    void exactly_minimum_value_passes() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("50.00"), 1));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void below_minimum_value_fails() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("49.99"), 1));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().getFirst()).contains("50,00");
    }

    @Test
    void exactly_max_items_passes() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("500.00"), 100));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void exceeds_max_items_fails() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("500.00"), 101));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().getFirst()).contains("100");
    }

    @Test
    void multiple_violations_return_all_errors() {
        ValidationResult result = strategy.validate(ctx(new BigDecimal("10.00"), 200));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(2);
    }
}
