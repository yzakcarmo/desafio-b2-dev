package com.yzakcarmo.desafiob2dev.service;

import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.api.dto.response.CreateOrderResponse;
import com.yzakcarmo.desafiob2dev.domain.entity.*;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderStatus;
import com.yzakcarmo.desafiob2dev.domain.repository.*;
import com.yzakcarmo.desafiob2dev.exception.*;
import com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.OrderEventPublisher;
import com.yzakcarmo.desafiob2dev.strategy.*;
import com.yzakcarmo.desafiob2dev.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String TENANT = "FARMA-DEFAULT";

    @Mock private OrderRepository orderRepository;
    @Mock private BuyerRepository buyerRepository;
    @Mock private SellerRepository sellerRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private ProductPriceRepository productPriceRepository;
    @Mock private PaymentConditionRepository paymentConditionRepository;
    @Mock private TenantStrategyRegistry strategyRegistry;
    @Mock private OrderEventPublisher eventPublisher;

    @Mock private OrderValidationStrategy validationStrategy;
    @Mock private OrderPricingStrategy pricingStrategy;
    @Mock private OrderDiscountStrategy discountStrategy;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setTenant() {
        TenantContext.setTenant(TENANT);
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Buyer buyer(BigDecimal creditLimit) {
        Buyer b = new Buyer();
        ReflectionTestUtils.setField(b, "id", UUID.randomUUID());
        b.setExternalReference("BUYER-001");
        b.setName("Farmácia Central");
        b.setCreditLimit(creditLimit);
        b.setTenantCode(TENANT);
        return b;
    }

    private Seller seller() {
        Seller s = new Seller();
        ReflectionTestUtils.setField(s, "id", UUID.randomUUID());
        s.setExternalReference("SELLER-001");
        s.setName("Distribuidora Norte");
        s.setTenantCode(TENANT);
        return s;
    }

    private Warehouse warehouse() {
        Warehouse w = new Warehouse();
        ReflectionTestUtils.setField(w, "id", UUID.randomUUID());
        w.setExternalReference("WH-001");
        w.setName("CD São Paulo");
        w.setTenantCode(TENANT);
        return w;
    }

    private PaymentCondition paymentCondition(int installments) {
        PaymentCondition pc = new PaymentCondition();
        ReflectionTestUtils.setField(pc, "id", UUID.randomUUID());
        pc.setCode("30-60-90");
        pc.setDescription("30/60/90 dias");
        pc.setMaxInstallments(installments);
        pc.setDiscountPercentage(BigDecimal.ZERO);
        pc.setTenantCode(TENANT);
        return pc;
    }

    private ProductPrice productPrice(String code, Warehouse warehouse) {
        ProductPrice pp = new ProductPrice();
        pp.setProductCode(code);
        pp.setProductName("Produto " + code);
        pp.setUnitPrice(new BigDecimal("10.00"));
        pp.setListPrice(new BigDecimal("15.00"));
        pp.setWarehouse(warehouse);
        pp.setTenantCode(TENANT);
        return pp;
    }

    private CreateOrderRequest request(String extRef, String productCode) {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setExternalReference(extRef);
        req.setBuyerReference("BUYER-001");
        req.setSellerReference("SELLER-001");
        req.setWarehouseReference("WH-001");
        req.setPaymentConditionCode("30-60-90");
        req.setItems(List.of(new CreateOrderRequest.OrderItemRequest(productCode, 5)));
        return req;
    }

    private Order savedOrder(Buyer b, Seller s, Warehouse w, PaymentCondition pc) {
        Order o = new Order();
        ReflectionTestUtils.setField(o, "id", UUID.randomUUID());
        o.setExternalReference("PED-001");
        o.setBuyer(b);
        o.setSeller(s);
        o.setWarehouse(w);
        o.setPaymentCondition(pc);
        o.setStatus(OrderStatus.CONFIRMED);
        o.setSubtotal(new BigDecimal("50.00"));
        o.setDiscountValue(BigDecimal.ZERO);
        o.setTotal(new BigDecimal("50.00"));
        o.setOrigin(OrderOrigin.API);
        o.setTenantCode(TENANT);
        return o;
    }

    private void stubStrategies(BigDecimal subtotal) {
        when(strategyRegistry.getValidation(TENANT)).thenReturn(validationStrategy);
        when(strategyRegistry.getPricing(TENANT)).thenReturn(pricingStrategy);
        when(strategyRegistry.getDiscount(TENANT)).thenReturn(discountStrategy);
        when(validationStrategy.validate(any())).thenReturn(ValidationResult.ok());
        when(pricingStrategy.calculate(any())).thenReturn(new PricingResult(subtotal, "Padrão"));
        when(discountStrategy.calculate(any(), any())).thenReturn(DiscountResult.noDiscount());
    }

    // -----------------------------------------------------------------------
    // createOrder — cenários de sucesso
    // -----------------------------------------------------------------------

    @Test
    void createOrder_success_returns_response() {
        Buyer b = buyer(new BigDecimal("500.00"));
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);
        ProductPrice price = productPrice("PROD-001", w);
        BigDecimal subtotal = new BigDecimal("50.00");

        when(orderRepository.existsByExternalReferenceAndTenantCode(any(), eq(TENANT))).thenReturn(false);
        when(buyerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue("BUYER-001", TENANT)).thenReturn(Optional.of(b));
        when(sellerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue("SELLER-001", TENANT)).thenReturn(Optional.of(s));
        when(warehouseRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue("WH-001", TENANT)).thenReturn(Optional.of(w));
        when(paymentConditionRepository.findByCodeAndTenantCodeAndEnabledTrue("30-60-90", TENANT)).thenReturn(Optional.of(pc));
        when(productPriceRepository.findByProductCodesAndWarehouse(any(), any(), eq(TENANT))).thenReturn(List.of(price));
        stubStrategies(subtotal);
        when(buyerRepository.findByIdForUpdate(b.getId(), TENANT)).thenReturn(Optional.of(b));
        when(buyerRepository.save(any())).thenReturn(b);
        when(orderRepository.save(any())).thenReturn(savedOrder(b, s, w, pc));
        doNothing().when(eventPublisher).publishOrderCreated(any(), any());

        CreateOrderResponse response = orderService.createOrder(request("PED-001", "PROD-001"), "API");

        assertThat(response).isNotNull();
        assertThat(response.getData().status()).isEqualTo("CONFIRMED");
        verify(orderRepository).save(any());
        verify(eventPublisher).publishOrderCreated(any(), any());
    }

    @Test
    void createOrder_deducts_credit_from_buyer() {
        BigDecimal initialCredit = new BigDecimal("200.00");
        BigDecimal orderTotal = new BigDecimal("50.00");

        Buyer b = buyer(initialCredit);
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);
        ProductPrice price = productPrice("PROD-001", w);

        when(orderRepository.existsByExternalReferenceAndTenantCode(any(), eq(TENANT))).thenReturn(false);
        when(buyerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(b));
        when(sellerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(s));
        when(warehouseRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(w));
        when(paymentConditionRepository.findByCodeAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(pc));
        when(productPriceRepository.findByProductCodesAndWarehouse(any(), any(), eq(TENANT))).thenReturn(List.of(price));
        stubStrategies(orderTotal);
        when(buyerRepository.findByIdForUpdate(b.getId(), TENANT)).thenReturn(Optional.of(b));
        when(buyerRepository.save(any())).thenReturn(b);
        when(orderRepository.save(any())).thenReturn(savedOrder(b, s, w, pc));
        doNothing().when(eventPublisher).publishOrderCreated(any(), any());

        orderService.createOrder(request("PED-001", "PROD-001"), "API");

        assertThat(b.getCreditLimit()).isEqualByComparingTo(initialCredit.subtract(orderTotal));
    }

    @Test
    void createOrder_bonus_order_skips_credit_check() {
        Buyer b = buyer(BigDecimal.ZERO);
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);
        ProductPrice price = productPrice("PROD-001", w);

        CreateOrderRequest req = request("PED-BONUS", "PROD-001");

        when(orderRepository.existsByExternalReferenceAndTenantCode(any(), eq(TENANT))).thenReturn(false);
        when(buyerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(b));
        when(sellerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(s));
        when(warehouseRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(w));
        when(paymentConditionRepository.findByCodeAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(pc));
        when(productPriceRepository.findByProductCodesAndWarehouse(any(), any(), eq(TENANT))).thenReturn(List.of(price));
        stubStrategies(new BigDecimal("50.00"));
        when(orderRepository.save(any())).thenReturn(savedOrder(b, s, w, pc));
        doNothing().when(eventPublisher).publishOrderCreated(any(), any());

        // BONUS origin: crédito zero não deve bloquear
        orderService.createOrder(req, "BONUS");

        verify(buyerRepository, never()).findByIdForUpdate(any(), any());
        assertThat(b.getCreditLimit()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // -----------------------------------------------------------------------
    // createOrder — cenários de erro
    // -----------------------------------------------------------------------

    @Test
    void createOrder_duplicate_reference_throws() {
        when(orderRepository.existsByExternalReferenceAndTenantCode("PED-DUP", TENANT)).thenReturn(true);

        assertThatThrownBy(() -> orderService.createOrder(request("PED-DUP", "PROD-001"), "API"))
                .isInstanceOf(DuplicateExternalReferenceException.class);
    }

    @Test
    void createOrder_buyer_not_found_throws() {
        when(orderRepository.existsByExternalReferenceAndTenantCode(any(), eq(TENANT))).thenReturn(false);
        when(buyerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request("PED-001", "PROD-001"), "API"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void createOrder_product_price_not_found_throws() {
        Buyer b = buyer(new BigDecimal("500.00"));
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);

        when(orderRepository.existsByExternalReferenceAndTenantCode(any(), eq(TENANT))).thenReturn(false);
        when(buyerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(b));
        when(sellerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(s));
        when(warehouseRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(w));
        when(paymentConditionRepository.findByCodeAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(pc));
        when(productPriceRepository.findByProductCodesAndWarehouse(any(), any(), eq(TENANT))).thenReturn(List.of()); // vazio

        assertThatThrownBy(() -> orderService.createOrder(request("PED-001", "PROD-INEXISTENTE"), "API"))
                .isInstanceOf(ProductPriceNotFoundException.class);
    }

    @Test
    void createOrder_insufficient_credit_throws() {
        Buyer b = buyer(new BigDecimal("10.00")); // crédito menor que o total
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);
        ProductPrice price = productPrice("PROD-001", w);
        BigDecimal subtotal = new BigDecimal("100.00"); // total > crédito

        when(orderRepository.existsByExternalReferenceAndTenantCode(any(), eq(TENANT))).thenReturn(false);
        when(buyerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(b));
        when(sellerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(s));
        when(warehouseRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(w));
        when(paymentConditionRepository.findByCodeAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(pc));
        when(productPriceRepository.findByProductCodesAndWarehouse(any(), any(), eq(TENANT))).thenReturn(List.of(price));
        stubStrategies(subtotal);
        when(buyerRepository.findByIdForUpdate(b.getId(), TENANT)).thenReturn(Optional.of(b));

        assertThatThrownBy(() -> orderService.createOrder(request("PED-001", "PROD-001"), "API"))
                .isInstanceOf(InsufficientCreditException.class);
    }

    @Test
    void createOrder_strategy_validation_failure_throws() {
        Buyer b = buyer(new BigDecimal("500.00"));
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);
        ProductPrice price = productPrice("PROD-001", w);

        when(orderRepository.existsByExternalReferenceAndTenantCode(any(), eq(TENANT))).thenReturn(false);
        when(buyerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(b));
        when(sellerRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(s));
        when(warehouseRepository.findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(w));
        when(paymentConditionRepository.findByCodeAndTenantCodeAndEnabledTrue(any(), eq(TENANT))).thenReturn(Optional.of(pc));
        when(productPriceRepository.findByProductCodesAndWarehouse(any(), any(), eq(TENANT))).thenReturn(List.of(price));
        when(strategyRegistry.getValidation(TENANT)).thenReturn(validationStrategy);
        when(validationStrategy.validate(any())).thenReturn(
                ValidationResult.failure(List.of("Pedido abaixo do valor mínimo")));

        assertThatThrownBy(() -> orderService.createOrder(request("PED-001", "PROD-001"), "API"))
                .isInstanceOf(TenantValidationException.class);

        // Pricing e Discount não devem ser executados após falha de validação
        verify(pricingStrategy, never()).calculate(any());
        verify(discountStrategy, never()).calculate(any(), any());
    }

    // -----------------------------------------------------------------------
    // cancelOrder
    // -----------------------------------------------------------------------

    @Test
    void cancelOrder_success_restores_credit_and_publishes_event() {
        Buyer b = buyer(new BigDecimal("100.00"));
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);

        Order order = savedOrder(b, s, w, pc);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotal(new BigDecimal("50.00"));
        order.setOrigin(OrderOrigin.API);

        when(orderRepository.findByExternalReferenceAndTenantCode("PED-001", TENANT)).thenReturn(Optional.of(order));
        when(buyerRepository.findByIdForUpdate(b.getId(), TENANT)).thenReturn(Optional.of(b));
        when(buyerRepository.save(any())).thenReturn(b);
        when(orderRepository.save(any())).thenReturn(order);
        doNothing().when(eventPublisher).publishOrderCancelled(any(), any());

        orderService.cancelOrder("PED-001");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(b.getCreditLimit()).isEqualByComparingTo("150.00"); // 100 + 50
        verify(eventPublisher).publishOrderCancelled(any(), any());
    }

    @Test
    void cancelOrder_already_cancelled_throws() {
        Buyer b = buyer(BigDecimal.ZERO);
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);

        Order order = savedOrder(b, s, w, pc);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findByExternalReferenceAndTenantCode("PED-001", TENANT)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder("PED-001"))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(buyerRepository, never()).findByIdForUpdate(any(), any());
    }

    @Test
    void cancelOrder_bonus_order_does_not_restore_credit() {
        Buyer b = buyer(new BigDecimal("100.00"));
        Seller s = seller();
        Warehouse w = warehouse();
        PaymentCondition pc = paymentCondition(3);

        Order order = savedOrder(b, s, w, pc);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrigin(OrderOrigin.BONUS);
        order.setTotal(new BigDecimal("500.00"));

        when(orderRepository.findByExternalReferenceAndTenantCode("PED-BONUS", TENANT)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);
        doNothing().when(eventPublisher).publishOrderCancelled(any(), any());

        orderService.cancelOrder("PED-BONUS");

        assertThat(b.getCreditLimit()).isEqualByComparingTo("100.00"); // crédito inalterado
        verify(buyerRepository, never()).findByIdForUpdate(any(), any());
    }

    @Test
    void cancelOrder_not_found_throws() {
        when(orderRepository.findByExternalReferenceAndTenantCode("PED-NAOEXISTE", TENANT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder("PED-NAOEXISTE"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
