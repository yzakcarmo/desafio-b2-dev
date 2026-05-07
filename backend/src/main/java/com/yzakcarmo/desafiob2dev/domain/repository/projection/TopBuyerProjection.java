package com.yzakcarmo.desafiob2dev.domain.repository.projection;

import java.math.BigDecimal;

public interface TopBuyerProjection {
    String getName();
    Long getOrderCount();
    BigDecimal getTotalSpent();
}