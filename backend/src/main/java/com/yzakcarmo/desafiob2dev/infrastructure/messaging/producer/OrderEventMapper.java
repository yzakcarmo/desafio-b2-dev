package com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer;

import com.yzakcarmo.desafiob2dev.domain.entity.Order;
import com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.dto.OrderEventPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "tenant", source = "tenantCode")
    OrderEventPayload toPayload(Order order);
}

