package com.yzakcarmo.desafiob2dev.strategy.discount;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.strategy.DiscountResult;
import com.yzakcarmo.desafiob2dev.strategy.OrderStrategyContext;
import com.yzakcarmo.desafiob2dev.strategy.PricingResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FarmaPremiumDiscountStrategyTest {

    private final FarmaPremiumDiscountStrategy strategy = new FarmaPremiumDiscountStrategy();

    private OrderStrategyContext ctx(int maxInstallments) {
        return new OrderStrategyContext("FARMA-PREMIUM", BigDecimal.ZERO, 1, maxInstallments, OrderOrigin.API, List.of());
    }

    private PricingResult pricing(String subtotal) {
        return new PricingResult(new BigDecimal(subtotal), "Precificação premium");
    }

    @Test
    void eight_percent_for_installment_payment() {
        DiscountResult result = strategy.calculate(ctx(3), pricing("1000.00"));

        assertThat(result.getDiscountValue()).isEqualByComparingTo("80.00");
        assertThat(result.getDiscountPercentage()).isEqualByComparingTo("8.00");
    }

    @Test
    void eleven_percent_for_single_installment() {
        DiscountResult result = strategy.calculate(ctx(1), pricing("1000.00"));

        assertThat(result.getDiscountValue()).isEqualByComparingTo("110.00");
        assertThat(result.getDiscountPercentage()).isEqualByComparingTo("11.00");
    }

    @Test
    void always_free_shipping_regardless_of_installments() {
        DiscountResult withInstallments = strategy.calculate(ctx(3), pricing("100.00"));
        DiscountResult atSight = strategy.calculate(ctx(1), pricing("100.00"));

        assertThat(withInstallments.isFreeShipping()).isTrue();
        assertThat(atSight.isFreeShipping()).isTrue();
    }

    @Test
    void description_reflects_payment_type() {
        DiscountResult installments = strategy.calculate(ctx(3), pricing("500.00"));
        DiscountResult atSight = strategy.calculate(ctx(1), pricing("500.00"));

        assertThat(installments.getDescription()).doesNotContain("à vista");
        assertThat(atSight.getDescription()).contains("à vista");
    }
}
