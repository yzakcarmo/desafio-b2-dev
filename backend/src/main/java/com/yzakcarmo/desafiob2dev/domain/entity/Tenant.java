package com.yzakcarmo.desafiob2dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant")
public class Tenant {

    @Id
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
