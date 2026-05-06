package com.yzakcarmo.desafiob2dev.service;

import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.api.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request, String tenant, String origin, String correlationId);
}
