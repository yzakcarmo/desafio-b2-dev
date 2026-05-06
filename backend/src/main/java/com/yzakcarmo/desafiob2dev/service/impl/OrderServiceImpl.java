package com.yzakcarmo.desafiob2dev.service.impl;

import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.api.dto.response.OrderResponse;
import com.yzakcarmo.desafiob2dev.api.mapper.OrderMapper;
import com.yzakcarmo.desafiob2dev.domain.entity.Order;
import com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.OrderEventMapper;
import com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.dto.OrderEventPayload;
import com.yzakcarmo.desafiob2dev.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderEventMapper orderEventMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderEventMapper orderEventMapper) {
        this.orderMapper = orderMapper;
        this.orderEventMapper = orderEventMapper;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request, String tenant, String origin, String correlationId) {
        Order order = orderMapper.toNewOrder(request, tenant, origin == null ? "API" : origin);
        OrderEventPayload eventPayload = orderEventMapper.toPayload(order);
        if (eventPayload.getTenant() == null || eventPayload.getTenant().isBlank()) {
            throw new IllegalStateException("Tenant inválido para evento de pedido");
        }
        return orderMapper.toResponse(order);
    }
}
