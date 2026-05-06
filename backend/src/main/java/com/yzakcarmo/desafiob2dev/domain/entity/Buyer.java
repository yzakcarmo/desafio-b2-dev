package com.yzakcarmo.desafiob2dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "buyer")
public class Buyer {

    @Id
    private UUID id;

    @Column(name = "external_reference", unique = true, nullable = false)
    private String externalReference;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "credit_limit", nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "tenant_code", nullable = false)
    private String tenantCode;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Version
    private Long version;
}
