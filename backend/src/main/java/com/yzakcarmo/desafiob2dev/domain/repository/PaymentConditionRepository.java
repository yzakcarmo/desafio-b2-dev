package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.PaymentCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentConditionRepository extends JpaRepository<PaymentCondition, UUID> {

    Optional<PaymentCondition> findByCodeAndTenantCodeAndEnabledTrue(
            String code, String tenantCode);
}