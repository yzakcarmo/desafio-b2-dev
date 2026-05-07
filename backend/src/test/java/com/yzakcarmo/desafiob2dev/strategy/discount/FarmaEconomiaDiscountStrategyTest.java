package com.yzakcarmo.desafiob2dev.strategy.discount;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.strategy.DiscountResult;
import com.yzakcarmo.desafiob2dev.strategy.OrderStrategyContext;
import com.yzakcarmo.desafiob2dev.strategy.PricingResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FarmaEconomiaDiscountStrategyTest {

    private final FarmaEconomiaDiscountStrategy strategy = new FarmaEconomiaDiscountStrategy();

    private OrderStrategyContext ctx(int maxInstallments) {
        return new OrderStrategyContext("FARMA-ECONOMIA", BigDecimal.ZERO, 1, maxInstallments, OrderOrigin.API, List.of());
    }

    private PricingResult pricing(String subtotal) {
        return new PricingResult(new BigDecimal(subtotal), "Precificação padrão");
    }

    @Test
    void no_discount_for_installment_payment() {
        DiscountResult result = strategy.calculate(ctx(3), pricing("500.00"));

        assertThat(result.getDiscountValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.isFreeShipping()).isFalse();
    }

    @Test
    void two_percent_discount_for_single_installment() {
        DiscountResult result = strategy.calculate(ctx(1), pricing("500.00"));

        assertThat(result.getDiscountValue()).isEqualByComparingTo("10.00");
        assertThat(result.getDiscountPercentage()).isEqualByComparingTo("2.00");
        assertThat(result.isFreeShipping()).isFalse();
    }

    @Test
    void discount_rounds_half_up() {
        DiscountResult result = strategy.calculate(ctx(1), pricing("333.33"));

        // 333.33 * 0.02 = 6.6666 → rounds to 6.67
        assertThat(result.getDiscountValue()).isEqualByComparingTo("6.67");
    }
}
