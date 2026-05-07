package com.yzakcarmo.desafiob2dev.api.controller;

import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.api.dto.response.*;
import com.yzakcarmo.desafiob2dev.service.OrderService;
import com.yzakcarmo.desafiob2dev.service.OrderStatisticsService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderStatisticsService statisticsService;

    public OrderController(OrderService orderService,
                           OrderStatisticsService statisticsService) {
        this.orderService = orderService;
        this.statisticsService = statisticsService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @RequestHeader(value = "x-origin", required = false) String origin) {

        CreateOrderResponse response = orderService.createOrder(request, origin);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderSummaryResponse>> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String buyerRef,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo) {

        return ResponseEntity.ok(
                orderService.listOrders(page, size, status, buyerRef, dateFrom, dateTo));
    }

    @GetMapping("/{externalReference}")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @PathVariable String externalReference) {

        return ResponseEntity.ok(orderService.getOrderDetail(externalReference));
    }

    @PostMapping("/{externalReference}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String externalReference) {
        orderService.cancelOrder(externalReference);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<OrderStatisticsResponse> getStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo) {

        return ResponseEntity.ok(statisticsService.getStatistics(dateFrom, dateTo));
    }
}