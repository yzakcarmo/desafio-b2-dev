package com.yzakcarmo.desafiob2dev.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Buyer {

    private UUID id;
    private String externalReference;
    private String name;
    private BigDecimal creditLimit;
    private String tenantCode;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private Long version;
}
