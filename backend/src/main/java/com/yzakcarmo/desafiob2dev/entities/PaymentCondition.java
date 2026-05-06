package com.yzakcarmo.desafiob2dev.entities;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentCondition {

    private UUID id;
    private String code;
    private String description;
    private Integer maxInstallment;
    private BigDecimal discountPercentage;
    private String tenantCode;
    private Boolean enabled;
}
