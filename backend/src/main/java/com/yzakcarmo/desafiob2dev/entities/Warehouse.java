package com.yzakcarmo.desafiob2dev.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Warehouse {

    private UUID id;
    private String externalReference;
    private String name;
    private UUID sellerId;
    private String tenantCode;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
