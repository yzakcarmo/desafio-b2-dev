package com.yzakcarmo.desafiob2dev.strategy;

import com.yzakcarmo.desafiob2dev.strategy.discount.FarmaDefaultDiscountStrategy;
import com.yzakcarmo.desafiob2dev.strategy.discount.FarmaEconomiaDiscountStrategy;
import com.yzakcarmo.desafiob2dev.strategy.discount.FarmaPremiumDiscountStrategy;
import com.yzakcarmo.desafiob2dev.strategy.pricing.FarmaDefaultPricingStrategy;
import com.yzakcarmo.desafiob2dev.strategy.pricing.FarmaEconomiaPricingStrategy;
import com.yzakcarmo.desafiob2dev.strategy.pricing.FarmaPremiumPricingStrategy;
import com.yzakcarmo.desafiob2dev.strategy.validation.FarmaDefaultValidationStrategy;
import com.yzakcarmo.desafiob2dev.strategy.validation.FarmaEconomiaValidationStrategy;
import com.yzakcarmo.desafiob2dev.strategy.validation.FarmaPremiumValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TenantStrategyRegistryTest {

    private TenantStrategyRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new TenantStrategyRegistry(
                List.of(
                        new FarmaDefaultValidationStrategy(),
                        new FarmaPremiumValidationStrategy(),
                        new FarmaEconomiaValidationStrategy()
                ),
                List.of(
                        new FarmaDefaultPricingStrategy(),
                        new FarmaPremiumPricingStrategy(),
                        new FarmaEconomiaPricingStrategy()
                ),
                List.of(
                        new FarmaDefaultDiscountStrategy(),
                        new FarmaPremiumDiscountStrategy(),
                        new FarmaEconomiaDiscountStrategy()
                )
        );
    }

    @Test
    void returns_correct_validation_strategy_per_tenant() {
        assertThat(registry.getValidation("FARMA-DEFAULT")).isInstanceOf(FarmaDefaultValidationStrategy.class);
        assertThat(registry.getValidation("FARMA-PREMIUM")).isInstanceOf(FarmaPremiumValidationStrategy.class);
        assertThat(registry.getValidation("FARMA-ECONOMIA")).isInstanceOf(FarmaEconomiaValidationStrategy.class);
    }

    @Test
    void returns_correct_pricing_strategy_per_tenant() {
        assertThat(registry.getPricing("FARMA-DEFAULT")).isInstanceOf(FarmaDefaultPricingStrategy.class);
        assertThat(registry.getPricing("FARMA-PREMIUM")).isInstanceOf(FarmaPremiumPricingStrategy.class);
        assertThat(registry.getPricing("FARMA-ECONOMIA")).isInstanceOf(FarmaEconomiaPricingStrategy.class);
    }

    @Test
    void returns_correct_discount_strategy_per_tenant() {
        assertThat(registry.getDiscount("FARMA-DEFAULT")).isInstanceOf(FarmaDefaultDiscountStrategy.class);
        assertThat(registry.getDiscount("FARMA-PREMIUM")).isInstanceOf(FarmaPremiumDiscountStrategy.class);
        assertThat(registry.getDiscount("FARMA-ECONOMIA")).isInstanceOf(FarmaEconomiaDiscountStrategy.class);
    }

    @Test
    void unknown_tenant_falls_back_to_farma_default_validation() {
        assertThat(registry.getValidation("TENANT-DESCONHECIDO")).isInstanceOf(FarmaDefaultValidationStrategy.class);
    }

    @Test
    void unknown_tenant_falls_back_to_farma_default_pricing() {
        assertThat(registry.getPricing("TENANT-DESCONHECIDO")).isInstanceOf(FarmaDefaultPricingStrategy.class);
    }

    @Test
    void unknown_tenant_falls_back_to_farma_default_discount() {
        assertThat(registry.getDiscount("TENANT-DESCONHECIDO")).isInstanceOf(FarmaDefaultDiscountStrategy.class);
    }
}
