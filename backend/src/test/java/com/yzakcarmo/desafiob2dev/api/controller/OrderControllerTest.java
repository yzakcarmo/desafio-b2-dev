package com.yzakcarmo.desafiob2dev.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.api.dto.response.*;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderStatus;
import com.yzakcarmo.desafiob2dev.exception.*;
import com.yzakcarmo.desafiob2dev.service.OrderService;
import com.yzakcarmo.desafiob2dev.service.OrderStatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean OrderService orderService;
    @MockitoBean OrderStatisticsService statisticsService;

    private static final String TENANT  = "FARMA-DEFAULT";
    private static final String AUTH    = "Bearer test-token";
    private static final String BASE    = "/api/v1/orders";

    // ─── POST /api/v1/orders ──────────────────────────────────────────────

    @Test
    void createOrder_success_returns201() throws Exception {
        when(orderService.createOrder(any(), any())).thenReturn(createOrderResponse());

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("ORD-001"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.externalReference").value("ORD-001"))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    void createOrder_missingTenantHeader_returns400() throws Exception {
        mockMvc.perform(post(BASE)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("ORD-001"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TENANT-001"));
    }

    @Test
    void createOrder_missingAuthorizationHeader_returns401() throws Exception {
        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("ORD-001"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH-001"));
    }

    @Test
    void createOrder_missingRequiredFields_returns422() throws Exception {
        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-001"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void createOrder_emptyItemsList_returns422() throws Exception {
        var request = validRequest("ORD-001");
        request.setItems(List.of());

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-001"));
    }

    @Test
    void createOrder_duplicateExternalReference_returns409() throws Exception {
        when(orderService.createOrder(any(), any()))
                .thenThrow(new DuplicateExternalReferenceException("ORD-DUP"));

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("ORD-DUP"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ORD-DUPLICATE-001"));
    }

    @Test
    void createOrder_insufficientCredit_returns422() throws Exception {
        when(orderService.createOrder(any(), any()))
                .thenThrow(new InsufficientCreditException(
                        new BigDecimal("100.00"), new BigDecimal("500.00")));

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("ORD-001"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-003"))
                .andExpect(jsonPath("$.details[0]").value("Crédito disponível: R$ 100.00"));
    }

    @Test
    void createOrder_buyerNotFound_returns422() throws Exception {
        when(orderService.createOrder(any(), any()))
                .thenThrow(new EntityNotFoundException("Buyer", "BUYER-999"));

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("ORD-001"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-002"))
                .andExpect(jsonPath("$.message").value("Buyer não encontrado(a): BUYER-999"));
    }

    @Test
    void createOrder_productNotFound_returns422() throws Exception {
        when(orderService.createOrder(any(), any()))
                .thenThrow(new ProductPriceNotFoundException("PROD-999"));

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("ORD-001"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-004"));
    }

    @Test
    void createOrder_tenantStrategyFailure_returns422() throws Exception {
        when(orderService.createOrder(any(), any()))
                .thenThrow(new TenantValidationException(
                        List.of("Pedido mínimo de R$ 200,00", "Máximo de 50 itens")));

        mockMvc.perform(post(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("ORD-001"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-005"))
                .andExpect(jsonPath("$.details[0]").value("Pedido mínimo de R$ 200,00"))
                .andExpect(jsonPath("$.details[1]").value("Máximo de 50 itens"));
    }

    // ─── GET /api/v1/orders ───────────────────────────────────────────────

    @Test
    void listOrders_success_returns200() throws Exception {
        when(orderService.listOrders(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(new PageResponse<>(List.of(), 0, 20, 0L, 0));

        mockMvc.perform(get(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listOrders_withStatusFilter_passesFilterToService() throws Exception {
        when(orderService.listOrders(eq(0), eq(20), eq("CONFIRMED"), any(), any(), any()))
                .thenReturn(new PageResponse<>(List.of(), 0, 20, 0L, 0));

        mockMvc.perform(get(BASE)
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk());

        verify(orderService).listOrders(0, 20, "CONFIRMED", null, null, null);
    }

    @Test
    void listOrders_missingTenantHeader_returns400() throws Exception {
        mockMvc.perform(get(BASE).header("Authorization", AUTH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TENANT-001"));
    }

    // ─── GET /api/v1/orders/{ref} ─────────────────────────────────────────

    @Test
    void getOrder_success_returns200() throws Exception {
        when(orderService.getOrderDetail("ORD-001")).thenReturn(null);

        mockMvc.perform(get(BASE + "/ORD-001")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH))
                .andExpect(status().isOk());
    }

    @Test
    void getOrder_notFound_returns422() throws Exception {
        when(orderService.getOrderDetail("ORD-999"))
                .thenThrow(new EntityNotFoundException("Order", "ORD-999"));

        mockMvc.perform(get(BASE + "/ORD-999")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-002"))
                .andExpect(jsonPath("$.message").value("Order não encontrado(a): ORD-999"));
    }

    // ─── POST /api/v1/orders/{ref}/cancel ─────────────────────────────────

    @Test
    void cancelOrder_success_returns204() throws Exception {
        doNothing().when(orderService).cancelOrder("ORD-001");

        mockMvc.perform(post(BASE + "/ORD-001/cancel")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelOrder_alreadyCancelled_returns422() throws Exception {
        doThrow(new InvalidStatusTransitionException("CANCELLED"))
                .when(orderService).cancelOrder("ORD-001");

        mockMvc.perform(post(BASE + "/ORD-001/cancel")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-STATUS-001"));
    }

    @Test
    void cancelOrder_deliveredOrder_returns422() throws Exception {
        doThrow(new InvalidStatusTransitionException("DELIVERED"))
                .when(orderService).cancelOrder("ORD-002");

        mockMvc.perform(post(BASE + "/ORD-002/cancel")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-STATUS-001"))
                .andExpect(jsonPath("$.message").value(
                        "Transição de status inválida. Status atual não permite cancelamento: DELIVERED"));
    }

    @Test
    void cancelOrder_notFound_returns422() throws Exception {
        doThrow(new EntityNotFoundException("Order", "ORD-999"))
                .when(orderService).cancelOrder("ORD-999");

        mockMvc.perform(post(BASE + "/ORD-999/cancel")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ORD-VALIDATION-002"));
    }

    // ─── GET /api/v1/orders/statistics ────────────────────────────────────

    @Test
    void getStatistics_success_returns200() throws Exception {
        var now = OffsetDateTime.now();
        var stats = new OrderStatisticsResponse(
                TENANT,
                new OrderStatisticsResponse.Period(now.minusDays(30), now),
                50L, 45L, 5L,
                new BigDecimal("25000.00"),
                new BigDecimal("500.00"),
                List.of(new OrderStatisticsResponse.TopBuyer("Farmácia Central", 10L, new BigDecimal("5000.00"))),
                List.of(new OrderStatisticsResponse.TopProduct("PROD-001", "Dipirona 500mg", 100L))
        );
        when(statisticsService.getStatistics(any(), any())).thenReturn(stats);

        mockMvc.perform(get(BASE + "/statistics")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .param("dateFrom", "2025-01-01T00:00:00Z")
                        .param("dateTo",   "2025-12-31T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenant").value(TENANT))
                .andExpect(jsonPath("$.totalOrders").value(50))
                .andExpect(jsonPath("$.confirmedOrders").value(45))
                .andExpect(jsonPath("$.cancelledOrders").value(5))
                .andExpect(jsonPath("$.topBuyers[0].name").value("Farmácia Central"))
                .andExpect(jsonPath("$.topProducts[0].productCode").value("PROD-001"));
    }

    @Test
    void getStatistics_missingDateFrom_returns400() throws Exception {
        mockMvc.perform(get(BASE + "/statistics")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .param("dateTo", "2025-12-31T23:59:59Z"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatistics_missingDateTo_returns400() throws Exception {
        mockMvc.perform(get(BASE + "/statistics")
                        .header("x-tenant", TENANT)
                        .header("Authorization", AUTH)
                        .param("dateFrom", "2025-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest());
    }

    private CreateOrderRequest validRequest(String externalRef) {
        var request = new CreateOrderRequest();
        request.setExternalReference(externalRef);
        request.setBuyerReference("BUYER-001");
        request.setSellerReference("SELLER-001");
        request.setWarehouseReference("WH-001");
        request.setPaymentConditionCode("A-VISTA");
        request.setItems(List.of(new CreateOrderRequest.OrderItemRequest("PROD-001", 5)));
        return request;
    }

    private CreateOrderResponse createOrderResponse() {
        return new CreateOrderResponse(
                "ORD-SUCCESS-001",
                "Pedido criado com sucesso",
                new CreateOrderResponse.Data(
                        UUID.randomUUID(),
                        "ORD-001",
                        OrderStatus.CONFIRMED.toString(),
                        new BigDecimal("42.50"),
                        BigDecimal.ZERO,
                        new BigDecimal("42.50"),
                        1,
                        new CreateOrderResponse.Validation(List.of()),
                        new CreateOrderResponse.Pricing(new BigDecimal("42.50"), "Precificação padrão"),
                        new CreateOrderResponse.Discount(BigDecimal.ZERO, BigDecimal.ZERO, "Sem desconto", false)
                )
        );
    }
}