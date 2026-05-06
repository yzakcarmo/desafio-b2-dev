package com.yzakcarmo.desafiob2dev.strategy;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TenantStrategyRegistry {

    private static final String DEFAULT_TENANT = "FARMA-DEFAULT";

    private final Map<String, OrderValidationStrategy> validationStrategies;
    private final Map<String, OrderPricingStrategy>    pricingStrategies;
    private final Map<String, OrderDiscountStrategy>   discountStrategies;

    public TenantStrategyRegistry(
            List<OrderValidationStrategy> validations,
            List<OrderPricingStrategy>    pricings,
            List<OrderDiscountStrategy>   discounts) {

        this.validationStrategies = indexByTenant(validations);
        this.pricingStrategies    = indexByTenant(pricings);
        this.discountStrategies   = indexByTenant(discounts);
    }

    public OrderValidationStrategy getValidation(String tenantCode) {
        return validationStrategies.getOrDefault(tenantCode,
                validationStrategies.get(DEFAULT_TENANT));
    }

    public OrderPricingStrategy getPricing(String tenantCode) {
        return pricingStrategies.getOrDefault(tenantCode,
                pricingStrategies.get(DEFAULT_TENANT));
    }

    public OrderDiscountStrategy getDiscount(String tenantCode) {
        return discountStrategies.getOrDefault(tenantCode,
                discountStrategies.get(DEFAULT_TENANT));
    }

    private <T> Map<String, T> indexByTenant(List<T> strategies) {
        return strategies.stream()
                .filter(s -> s.getClass().isAnnotationPresent(TenantStrategy.class))
                .collect(Collectors.toMap(
                        s -> s.getClass().getAnnotation(TenantStrategy.class).tenantCode(),
                        Function.identity()
                ));
    }
}