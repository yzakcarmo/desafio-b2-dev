package com.yzakcarmo.desafiob2dev.api.dto.response;

import java.math.BigDecimal;

public record ProductListResponse (
        String label,
        String value,
        BigDecimal price
) {}
