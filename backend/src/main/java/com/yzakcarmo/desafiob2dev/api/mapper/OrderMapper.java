package com.yzakcarmo.desafiob2dev.api.mapper;

import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.api.dto.response.OrderResponse;
import com.yzakcarmo.desafiob2dev.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {java.util.UUID.class, java.time.LocalDateTime.class, java.math.BigDecimal.class})
public interface OrderMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "externalReference", source = "request.externalReference")
    @Mapping(target = "buyerId", ignore = true)
    @Mapping(target = "sellerId", ignore = true)
    @Mapping(target = "warehouseId", ignore = true)
    @Mapping(target = "paymentConditionId", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "subtotal", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "discountValue", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "total", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "origin", source = "origin")
    @Mapping(target = "tenantCode", source = "tenantCode")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "lastModified", expression = "java(LocalDateTime.now())")
    @Mapping(target = "version", constant = "0L")
    Order toNewOrder(CreateOrderRequest request, String tenantCode, String origin);

    @Mapping(target = "orderId", source = "id")
    OrderResponse toResponse(Order order);
}

