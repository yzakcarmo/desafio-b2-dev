package com.yzakcarmo.desafiob2dev.domain.repository.projection;

import java.math.BigDecimal;

public interface ProductListProjection {
    String getExternalReference();
    String getName();
    BigDecimal getPrice();
}
