package com.yzakcarmo.desafiob2dev.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Seller {

    private UUID id;
    private String externalReference;
    private String name;
    private String tenantCode;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
