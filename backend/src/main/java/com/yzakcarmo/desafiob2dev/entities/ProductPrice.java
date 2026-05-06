package com.yzakcarmo.desafiob2dev.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductPrice {

    private UUID id;
    private String productCode;
    private String productName;
    private UUID warehouseId;
    private BigDecimal unitPrice;
    private BigDecimal listPrice;
    private String tenantCode;
    private Boolean enabled;
    private LocalDateTime lastModified;
}
