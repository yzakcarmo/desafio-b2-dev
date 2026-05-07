package com.yzakcarmo.desafiob2dev.strategy.discount;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.strategy.DiscountResult;
import com.yzakcarmo.desafiob2dev.strategy.OrderStrategyContext;
import com.yzakcarmo.desafiob2dev.strategy.PricingResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FarmaDefaultDiscountStrategyTest {

    private final FarmaDefaultDiscountStrategy strategy = new FarmaDefaultDiscountStrategy();

    private PricingResult pricing(String subtotal) {
        return new PricingResult(new BigDecimal(subtotal), "Precificação padrão");
    }

    private OrderStrategyContext ctx(int maxInstallments) {
        return new OrderStrategyContext("FARMA-DEFAULT", BigDecimal.ZERO, 1, maxInstallments, OrderOrigin.API, List.of());
    }

    @Test
    void below_threshold_no_discount() {
        DiscountResult result = strategy.calculate(ctx(3), pricing("499.99"));

        assertThat(result.getDiscountValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.isFreeShipping()).isFalse();
    }

    @Test
    void exactly_at_threshold_applies_five_percent() {
        DiscountResult result = strategy.calculate(ctx(3), pricing("500.00"));

        assertThat(result.getDiscountValue()).isEqualByComparingTo("25.00");
        assertThat(result.getDiscountPercentage()).isEqualByComparingTo("5.00");
        assertThat(result.isFreeShipping()).isFalse();
    }

    @Test
    void above_threshold_applies_five_percent() {
        DiscountResult result = strategy.calculate(ctx(1), pricing("600.00"));

        assertThat(result.getDiscountValue()).isEqualByComparingTo("30.00");
    }

    @Test
    void below_free_shipping_threshold_no_free_shipping() {
        DiscountResult result = strategy.calculate(ctx(1), pricing("999.99"));

        assertThat(result.isFreeShipping()).isFalse();
    }

    @Test
    void exactly_at_free_shipping_threshold_grants_free_shipping() {
        DiscountResult result = strategy.calculate(ctx(1), pricing("1000.00"));

        assertThat(result.isFreeShipping()).isTrue();
        assertThat(result.getDiscountValue()).isEqualByComparingTo("50.00");
    }

    @Test
    void above_free_shipping_threshold_grants_free_shipping() {
        DiscountResult result = strategy.calculate(ctx(1), pricing("1500.00"));

        assertThat(result.isFreeShipping()).isTrue();
        assertThat(result.getDiscountValue()).isEqualByComparingTo("75.00");
    }
}
