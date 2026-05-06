package com.yzakcarmo.desafiob2dev.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tenant {

    private UUID id;
    private String code;
    private String name;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
