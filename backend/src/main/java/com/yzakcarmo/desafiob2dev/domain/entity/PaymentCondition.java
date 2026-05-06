package com.yzakcarmo.desafiob2dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_condition")
public class PaymentCondition {

    @Id
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "max_installments", nullable = false)
    private Integer maxInstallments;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Column(name = "tenant_code", nullable = false)
    private String tenantCode;

    @Column(name = "enabled")
    private Boolean enabled;
}
