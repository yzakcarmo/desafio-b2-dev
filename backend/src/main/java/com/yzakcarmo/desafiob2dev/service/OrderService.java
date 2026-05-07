package com.yzakcarmo.desafiob2dev.service;

import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.api.dto.response.*;
import com.yzakcarmo.desafiob2dev.domain.entity.*;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderStatus;
import com.yzakcarmo.desafiob2dev.domain.repository.*;
import com.yzakcarmo.desafiob2dev.exception.*;
import com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.OrderEventPublisher;
import com.yzakcarmo.desafiob2dev.strategy.*;
import com.yzakcarmo.desafiob2dev.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductPriceRepository productPriceRepository;
    private final PaymentConditionRepository paymentConditionRepository;
    private final TenantStrategyRegistry strategyRegistry;
    private final OrderEventPublisher eventPublisher;

    public OrderService(
            OrderRepository orderRepository,
            BuyerRepository buyerRepository,
            SellerRepository sellerRepository,
            WarehouseRepository warehouseRepository,
            ProductPriceRepository productPriceRepository,
            PaymentConditionRepository paymentConditionRepository,
            TenantStrategyRegistry strategyRegistry,
            OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
        this.warehouseRepository = warehouseRepository;
        this.productPriceRepository = productPriceRepository;
        this.paymentConditionRepository = paymentConditionRepository;
        this.strategyRegistry = strategyRegistry;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, String origin) {
        String tenantCode = TenantContext.getTenant();

        // 1. Verificar duplicata
        if (orderRepository.existsByExternalReferenceAndTenantCode(
                request.getExternalReference(), tenantCode)) {
            throw new DuplicateExternalReferenceException(request.getExternalReference());
        }

        // 2. Buscar e validar entidades
        Buyer buyer = buyerRepository
                .findByExternalReferenceAndTenantCodeAndEnabledTrue(
                        request.getBuyerReference(), tenantCode)
                .orElseThrow(() -> new EntityNotFoundException("Buyer", request.getBuyerReference()));

        Seller seller = sellerRepository
                .findByExternalReferenceAndTenantCodeAndEnabledTrue(
                        request.getSellerReference(), tenantCode)
                .orElseThrow(() -> new EntityNotFoundException("Seller", request.getSellerReference()));

        Warehouse warehouse = warehouseRepository
                .findByExternalReferenceAndTenantCodeAndEnabledTrue(
                        request.getWarehouseReference(), tenantCode)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse", request.getWarehouseReference()));

        PaymentCondition paymentCondition = paymentConditionRepository
                .findByCodeAndTenantCodeAndEnabledTrue(
                        request.getPaymentConditionCode(), tenantCode)
                .orElseThrow(() -> new EntityNotFoundException("PaymentCondition",
                        request.getPaymentConditionCode()));

        // 3. Buscar preços no banco — nunca do cliente
        List<String> productCodes = request.getItems().stream()
                .map(CreateOrderRequest.OrderItemRequest::productCode)
                .toList();

        List<ProductPrice> prices = productPriceRepository
                .findByProductCodesAndWarehouse(productCodes, warehouse.getId(), tenantCode);

        Map<String, ProductPrice> priceMap = prices.stream()
                .collect(Collectors.toMap(ProductPrice::getProductCode, Function.identity()));

        // Verificar se todos os produtos têm preço cadastrado
        for (String code : productCodes) {
            if (!priceMap.containsKey(code)) {
                throw new ProductPriceNotFoundException(code);
            }
        }

        // 4. Montar contexto para as strategies
        List<OrderStrategyContext.ItemContext> itemContexts = request.getItems().stream()
                .map(item -> {
                    ProductPrice price = priceMap.get(item.productCode());
                    return new OrderStrategyContext.ItemContext(
                            item.productCode(),
                            item.quantity(),
                            price.getUnitPrice(),
                            price.getListPrice()
                    );
                }).toList();

        BigDecimal rawSubtotal = itemContexts.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = request.getItems().stream()
                .mapToInt(CreateOrderRequest.OrderItemRequest::quantity)
                .sum();

        OrderOrigin orderOrigin = parseOrigin(origin);

        OrderStrategyContext strategyContext = new OrderStrategyContext(
                tenantCode,
                rawSubtotal,
                totalItems,
                paymentCondition.getMaxInstallments(),
                orderOrigin,
                itemContexts
        );

        // 5. Executar strategies em sequência
        ValidationResult validation = strategyRegistry
                .getValidation(tenantCode).validate(strategyContext);

        if (!validation.isValid()) {
            throw new TenantValidationException(validation.getErrors());
        }

        PricingResult pricing = strategyRegistry
                .getPricing(tenantCode).calculate(strategyContext);

        DiscountResult discount = strategyRegistry
                .getDiscount(tenantCode).calculate(strategyContext, pricing);

        BigDecimal total = pricing.getSubtotal().subtract(discount.getDiscountValue());

        // 6. Verificar e decrementar crédito atomicamente
        boolean isBonusOrder = OrderOrigin.BONUS.equals(orderOrigin);
        if (!isBonusOrder) {
            Buyer buyerForUpdate = buyerRepository
                    .findByIdForUpdate(buyer.getId(), tenantCode)
                    .orElseThrow(() -> new EntityNotFoundException("Buyer", request.getBuyerReference()));

            if (buyerForUpdate.getCreditLimit().compareTo(total) < 0) {
                throw new InsufficientCreditException(buyerForUpdate.getCreditLimit(), total);
            }

            buyerForUpdate.setCreditLimit(buyerForUpdate.getCreditLimit().subtract(total));
            buyerRepository.save(buyerForUpdate);
            buyer = buyerForUpdate;
        }

        // 7. Persistir pedido
        Order order = new Order();
        order.setExternalReference(request.getExternalReference());
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setWarehouse(warehouse);
        order.setPaymentCondition(paymentCondition);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setSubtotal(pricing.getSubtotal());
        order.setDiscountValue(discount.getDiscountValue());
        order.setTotal(total);
        order.setOrigin(orderOrigin);
        order.setTenantCode(tenantCode);

        request.getItems().forEach(itemRequest -> {
            ProductPrice price = priceMap.get(itemRequest.productCode());
            OrderItem item = new OrderItem();
            item.setProductCode(price.getProductCode());
            item.setProductName(price.getProductName());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(price.getUnitPrice());
            item.setListPrice(price.getListPrice());
            item.setSubtotal(price.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity())));
            order.addItem(item);
        });

        Order saved = orderRepository.save(order);

        eventPublisher.publishOrderCreated(saved, randomUUID().toString());

        return new CreateOrderResponse(
                "ORDER-001",
                "Pedido criado com sucesso",
                new CreateOrderResponse.Data(
                        saved.getId(),
                        saved.getExternalReference(),
                        saved.getStatus().name(),
                        saved.getSubtotal(),
                        saved.getDiscountValue(),
                        saved.getTotal(),
                        saved.getItems().size(),
                        new CreateOrderResponse.Validation(validation.getWarnings()),
                        new CreateOrderResponse.Pricing(pricing.getSubtotal(), pricing.getDescription()),
                        new CreateOrderResponse.Discount(
                                discount.getDiscountValue(),
                                discount.getDiscountPercentage(),
                                discount.getDescription(),
                                discount.isFreeShipping()
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> listOrders(
            int page, int size, String status, String buyerRef,
            OffsetDateTime dateFrom, OffsetDateTime dateTo) {

        String tenantCode = TenantContext.getTenant();
        size = Math.min(size, 50);

        OrderStatus orderStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                orderStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("ORD-VALIDATION-001",
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Status inválido: " + status);
            }
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Order> spec = OrderSpecification.withFilters(
                tenantCode, orderStatus, buyerRef, dateFrom, dateTo);

        // Query 1: orders paginadas com buyer, seller, warehouse
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        // Query 2: itens apenas dos IDs retornados
        List<UUID> orderIds = orderPage.getContent().stream()
                .map(Order::getId).toList();

        Map<UUID, Integer> itemCountMap = orderIds.isEmpty()
                ? Map.of()
                : orderRepository.findAllWithItemsByIds(orderIds).stream()
                  .collect(Collectors.toMap(Order::getId, o -> o.getItems().size()));

        List<OrderSummaryResponse> content = orderPage.getContent().stream()
                .map(o -> new OrderSummaryResponse(
                        o.getId(),
                        o.getExternalReference(),
                        o.getBuyer().getName(),
                        o.getSeller().getName(),
                        o.getWarehouse().getName(),
                        o.getStatus().name(),
                        o.getSubtotal(),
                        o.getDiscountValue(),
                        o.getTotal(),
                        itemCountMap.getOrDefault(o.getId(), 0),
                        o.getOrigin().name(),
                        o.getCreatedAt()
                )).toList();

        return new PageResponse<>(
                content,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(String externalReference) {
        String tenantCode = TenantContext.getTenant();

        Order order = orderRepository
                .findDetailByExternalReference(externalReference, tenantCode)
                .orElseThrow(() -> new EntityNotFoundException("Order", externalReference));

        return toDetailResponse(order);
    }

    @Transactional
    public void cancelOrder(String externalReference) {
        String tenantCode = TenantContext.getTenant();

        Order order = orderRepository
                .findByExternalReferenceAndTenantCode(externalReference, tenantCode)
                .orElseThrow(() -> new EntityNotFoundException("Order", externalReference));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidStatusTransitionException(order.getStatus().name());
        }

        // Restaurar crédito atomicamente
        boolean isBonusOrder = OrderOrigin.BONUS.equals(order.getOrigin());
        if (!isBonusOrder) {
            Buyer buyer = buyerRepository
                    .findByIdForUpdate(order.getBuyer().getId(), tenantCode)
                    .orElseThrow(() -> new EntityNotFoundException("Buyer",
                            order.getBuyer().getExternalReference()));

            buyer.setCreditLimit(buyer.getCreditLimit().add(order.getTotal()));
            buyerRepository.save(buyer);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        eventPublisher.publishOrderCancelled(order, randomUUID().toString());
    }

    private OrderDetailResponse toDetailResponse(Order order) {
        return new OrderDetailResponse(
                order.getId(),
                order.getExternalReference(),
                order.getStatus().name(),
                order.getOrigin().name(),
                order.getSubtotal(),
                order.getDiscountValue(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getLastModified(),
                new OrderDetailResponse.BuyerInfo(
                        order.getBuyer().getExternalReference(),
                        order.getBuyer().getName()),
                new OrderDetailResponse.SellerInfo(
                        order.getSeller().getExternalReference(),
                        order.getSeller().getName()),
                new OrderDetailResponse.WarehouseInfo(
                        order.getWarehouse().getExternalReference(),
                        order.getWarehouse().getName()),
                new OrderDetailResponse.PaymentConditionInfo(
                        order.getPaymentCondition().getCode(),
                        order.getPaymentCondition().getDescription(),
                        order.getPaymentCondition().getMaxInstallments()),
                order.getItems().stream()
                        .map(i -> new OrderDetailResponse.ItemInfo(
                                i.getProductCode(),
                                i.getProductName(),
                                i.getQuantity(),
                                i.getUnitPrice(),
                                i.getListPrice(),
                                i.getSubtotal()))
                        .toList()
        );
    }

    private OrderOrigin parseOrigin(String origin) {
        if (origin == null || origin.isBlank()) return OrderOrigin.API;
        try {
            return OrderOrigin.valueOf(origin.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OrderOrigin.API;
        }
    }
}