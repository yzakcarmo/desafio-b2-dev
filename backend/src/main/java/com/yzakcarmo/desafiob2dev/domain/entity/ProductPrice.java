package com.yzakcarmo.desafiob2dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_price")
public class ProductPrice {

    @Id
    private UUID id;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "list_price", nullable = false)
    private BigDecimal listPrice;

    @Column(name = "tenant_code", nullable = false)
    private String tenantCode;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;
}
