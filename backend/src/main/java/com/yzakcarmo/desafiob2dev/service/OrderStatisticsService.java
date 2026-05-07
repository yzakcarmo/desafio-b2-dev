package com.yzakcarmo.desafiob2dev.service;

import com.yzakcarmo.desafiob2dev.api.dto.response.OrderStatisticsResponse;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderStatus;
import com.yzakcarmo.desafiob2dev.domain.repository.OrderRepository;
import com.yzakcarmo.desafiob2dev.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OrderStatisticsService {

    private final OrderRepository orderRepository;

    public OrderStatisticsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public OrderStatisticsResponse getStatistics(OffsetDateTime dateFrom, OffsetDateTime dateTo) {
        String tenantCode = TenantContext.getTenant();

        long confirmed  = orderRepository.countByStatus(tenantCode, OrderStatus.CONFIRMED,  dateFrom, dateTo);
        long cancelled  = orderRepository.countByStatus(tenantCode, OrderStatus.CANCELLED,  dateFrom, dateTo);
        long totalOrders = confirmed + cancelled;

        BigDecimal revenue = orderRepository.sumRevenue(tenantCode, dateFrom, dateTo);
        BigDecimal avg     = orderRepository.avgOrderValue(tenantCode, dateFrom, dateTo);

        List<OrderStatisticsResponse.TopBuyer> topBuyers = orderRepository
                .findTopBuyers(tenantCode, dateFrom, dateTo)
                .stream()
                .map(p -> new OrderStatisticsResponse.TopBuyer(
                        p.getName(), p.getOrderCount(), p.getTotalSpent()))
                .toList();

        List<OrderStatisticsResponse.TopProduct> topProducts = orderRepository
                .findTopProducts(tenantCode, dateFrom, dateTo)
                .stream()
                .map(p -> new OrderStatisticsResponse.TopProduct(
                        p.getProductCode(), p.getProductName(), p.getTotalQuantity()))
                .toList();

        return new OrderStatisticsResponse(
                tenantCode,
                new OrderStatisticsResponse.Period(dateFrom, dateTo),
                totalOrders,
                confirmed,
                cancelled,
                revenue.setScale(2, RoundingMode.HALF_UP),
                avg.setScale(2, RoundingMode.HALF_UP),
                topBuyers,
                topProducts
        );
    }
}