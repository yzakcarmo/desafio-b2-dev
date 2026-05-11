package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.PaymentCondition;
import com.yzakcarmo.desafiob2dev.domain.repository.projection.ListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentConditionRepository extends JpaRepository<PaymentCondition, UUID> {

    Optional<PaymentCondition> findByCodeAndTenantCodeAndEnabledTrue(
            String code, String tenantCode);

    @Query("""
        SELECT p.code AS externalReference,
               p.description AS name
        FROM PaymentCondition p
        WHERE p.tenantCode = :tenant AND p.enabled = true
        """)
    List<ListProjection> listAllToOrder(@Param("tenant") String tenant);
}