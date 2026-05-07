package com.yzakcarmo.desafiob2dev.strategy.validation;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.strategy.OrderStrategyContext;
import com.yzakcarmo.desafiob2dev.strategy.ValidationResult;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

class FarmaEconomiaValidationStrategyTest {

    private static final ZoneId BRAZIL = ZoneId.of("America/Sao_Paulo");

    // Instâncias pré-computadas fora de qualquer MockedStatic para evitar
    // que ZonedDateTime.of() seja interceptado no meio do chain de stubbing.
    private static final ZonedDateTime INSIDE_HOURS  = ZonedDateTime.of(2025, 6, 15, 10, 0, 0, 0, BRAZIL);
    private static final ZonedDateTime BEFORE_HOURS  = ZonedDateTime.of(2025, 6, 15,  7, 0, 0, 0, BRAZIL);
    private static final ZonedDateTime AFTER_HOURS   = ZonedDateTime.of(2025, 6, 15, 18, 0, 0, 0, BRAZIL);
    private static final ZonedDateTime LATE_NIGHT    = ZonedDateTime.of(2025, 6, 15, 20, 0, 0, 0, BRAZIL);

    private final FarmaEconomiaValidationStrategy strategy = new FarmaEconomiaValidationStrategy();

    private OrderStrategyContext ctx(BigDecimal subtotal, int totalItems) {
        return new OrderStrategyContext("FARMA-ECONOMIA", subtotal, totalItems, 1, OrderOrigin.API, List.of());
    }

    @Test
    void valid_during_business_hours() {
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class, CALLS_REAL_METHODS)) {
            mocked.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(INSIDE_HOURS);

            ValidationResult result = strategy.validate(ctx(new BigDecimal("300.00"), 5));

            assertThat(result.isValid()).isTrue();
        }
    }

    @Test
    void before_business_hours_fails() {
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class, CALLS_REAL_METHODS)) {
            mocked.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(BEFORE_HOURS);

            ValidationResult result = strategy.validate(ctx(new BigDecimal("300.00"), 5));

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).anyMatch(e -> e.contains("horário comercial"));
        }
    }

    @Test
    void at_18h_is_outside_business_hours() {
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class, CALLS_REAL_METHODS)) {
            mocked.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(AFTER_HOURS);

            ValidationResult result = strategy.validate(ctx(new BigDecimal("300.00"), 5));

            assertThat(result.isValid()).isFalse();
        }
    }

    @Test
    void below_minimum_value_fails() {
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class, CALLS_REAL_METHODS)) {
            mocked.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(INSIDE_HOURS);

            ValidationResult result = strategy.validate(ctx(new BigDecimal("199.99"), 5));

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).anyMatch(e -> e.contains("200,00"));
        }
    }

    @Test
    void exceeds_max_items_fails() {
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class, CALLS_REAL_METHODS)) {
            mocked.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(INSIDE_HOURS);

            ValidationResult result = strategy.validate(ctx(new BigDecimal("500.00"), 51));

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).anyMatch(e -> e.contains("50"));
        }
    }

    @Test
    void all_violations_reported_together() {
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class, CALLS_REAL_METHODS)) {
            mocked.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(LATE_NIGHT);

            ValidationResult result = strategy.validate(ctx(new BigDecimal("10.00"), 100));

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).hasSize(3);
        }
    }
}
