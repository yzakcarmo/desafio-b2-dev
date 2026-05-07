package com.yzakcarmo.desafiob2dev.domain.repository.projection;

public interface TopProductProjection {
    String getProductCode();
    String getProductName();
    Long getTotalQuantity();
}