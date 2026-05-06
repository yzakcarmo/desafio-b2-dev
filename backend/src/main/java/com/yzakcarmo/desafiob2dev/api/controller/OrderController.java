package com.yzakcarmo.desafiob2dev.api.controller;

import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.api.dto.response.OrderResponse;
import com.yzakcarmo.desafiob2dev.service.OrderService;
import com.yzakcarmo.desafiob2dev.tenant.TenantContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader(value = "x-origin", required = false) String origin,
            @RequestHeader(value = "x-correlation-id", required = false) String correlationId,
            @Valid @RequestBody CreateOrderRequest request) {

        String tenant = TenantContext.getTenant();
        OrderResponse resp = orderService.createOrder(request, tenant, origin == null ? "API" : origin, correlationId);
        return ResponseEntity.status(201).body(resp);
    }
}
