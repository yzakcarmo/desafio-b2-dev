package com.yzakcarmo.desafiob2dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "seller")
public class Seller {

    @Id
    private UUID id;

    @Column(name = "external_reference", unique = true, nullable = false)
    private String externalReference;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tenant_code", nullable = false)
    private String tenantCode;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
