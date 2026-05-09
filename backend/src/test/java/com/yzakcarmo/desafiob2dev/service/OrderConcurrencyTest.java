package com.yzakcarmo.desafiob2dev.service;

import com.yzakcarmo.desafiob2dev.api.dto.request.CreateOrderRequest;
import com.yzakcarmo.desafiob2dev.domain.entity.*;
import com.yzakcarmo.desafiob2dev.domain.repository.*;
import com.yzakcarmo.desafiob2dev.exception.InsufficientCreditException;
import com.yzakcarmo.desafiob2dev.infrastructure.messaging.producer.OrderEventPublisher;
import com.yzakcarmo.desafiob2dev.strategy.*;
import com.yzakcarmo.desafiob2dev.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Simula PESSIMISTIC_WRITE (SELECT FOR UPDATE) via ReentrantLock no mock
 * de findByIdForUpdate. O crédito é rastreado com AtomicReference para
 * verificar que nunca vai a negativo e que exatamente N pedidos são aprovados.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderConcurrencyTest {

    private static final String TENANT          = "FARMA-DEFAULT";
    private static final int    THREADS         = 10;
    private static final int    CREDIT_ORDERS   = 3;  // crédito suficiente para 3 pedidos
    private static final BigDecimal ORDER_AMOUNT = new BigDecimal("100.00");
    private static final UUID   BUYER_ID        = UUID.fromString("00000000-0000-0000-0003-000000000001");

    @Mock private OrderRepository              orderRepository;
    @Mock private BuyerRepository              buyerRepository;
    @Mock private SellerRepository             sellerRepository;
    @Mock private WarehouseRepository          warehouseRepository;
    @Mock private ProductPriceRepository       productPriceRepository;
    @Mock private PaymentConditionRepository   paymentConditionRepository;
    @Mock private TenantStrategyRegistry       strategyRegistry;
    @Mock private OrderEventPublisher          eventPublisher;
    @Mock private OrderValidationStrategy      validationStrategy;
    @Mock private OrderPricingStrategy         pricingStrategy;
    @Mock private OrderDiscountStrategy        discountStrategy;

    @InjectMocks private OrderService orderService;

    // Estado compartilhado entre threads (simula a linha do banco)
    private AtomicReference<BigDecimal> dbCredit;
    private ReentrantLock               rowLock;

    @BeforeEach
    void setUp() {
        BigDecimal initialCredit = ORDER_AMOUNT.multiply(BigDecimal.valueOf(CREDIT_ORDERS));
        dbCredit = new AtomicReference<>(initialCredit);
        rowLock  = new ReentrantLock();

        // ── Entidades fixas (seller, warehouse, payment condition, preço) ──────
        Seller seller = buildSeller();
        Warehouse warehouse = buildWarehouse();
        PaymentCondition pc = buildPaymentCondition();
        ProductPrice price = buildProductPrice(warehouse);

        when(sellerRepository
                .findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), any()))
                .thenReturn(Optional.of(seller));
        when(warehouseRepository
                .findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), any()))
                .thenReturn(Optional.of(warehouse));
        when(paymentConditionRepository
                .findByCodeAndTenantCodeAndEnabledTrue(any(), any()))
                .thenReturn(Optional.of(pc));
        when(productPriceRepository
                .findByProductCodesAndWarehouse(any(), any(), any()))
                .thenReturn(List.of(price));

        // Sem duplicata de externalReference
        when(orderRepository
                .existsByExternalReferenceAndTenantCode(any(), any()))
                .thenReturn(false);

        // ── Strategies: sem restrição de negócio, subtotal = ORDER_AMOUNT ─────
        when(strategyRegistry.getValidation(any())).thenReturn(validationStrategy);
        when(strategyRegistry.getPricing(any())).thenReturn(pricingStrategy);
        when(strategyRegistry.getDiscount(any())).thenReturn(discountStrategy);
        when(validationStrategy.validate(any())).thenReturn(ValidationResult.ok());
        when(pricingStrategy.calculate(any()))
                .thenReturn(new PricingResult(ORDER_AMOUNT, "Padrão"));
        when(discountStrategy.calculate(any(), any()))
                .thenReturn(DiscountResult.noDiscount());

        // ── Buyer: primeira busca (enabled check) ─────────────────────────────
        Buyer staticBuyer = buildBuyer(BUYER_ID, dbCredit.get());
        when(buyerRepository
                .findByExternalReferenceAndTenantCodeAndEnabledTrue(any(), any()))
                .thenReturn(Optional.of(staticBuyer));

        // ── findByIdForUpdate: adquire o lock de linha (simula PESSIMISTIC_WRITE)
        //    e devolve o crédito atual para que o service faça a verificação.
        when(buyerRepository.findByIdForUpdate(any(), any())).thenAnswer(inv -> {
            rowLock.lock();                          // bloqueia até o thread anterior liberar
            return Optional.of(buildBuyer(BUYER_ID, dbCredit.get()));
        });

        // ── save(Buyer): persiste o novo crédito e libera o lock ──────────────
        when(buyerRepository.save(any(Buyer.class))).thenAnswer(inv -> {
            try {
                Buyer b = inv.getArgument(0);
                dbCredit.set(b.getCreditLimit());    // commit do novo saldo
                return b;
            } finally {
                if (rowLock.isHeldByCurrentThread()) rowLock.unlock();
            }
        });

        // ── save(Order): devolve pedido com ID gerado ─────────────────────────
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            ReflectionTestUtils.setField(o, "id", UUID.randomUUID());
            return o;
        });
    }

    @RepeatedTest(3)
    void tenThreadsSameBuyer_onlyThreeSucceed_creditNeverGoesNegative() throws Exception {
        ExecutorService executor   = Executors.newFixedThreadPool(THREADS);
        CountDownLatch  startLatch = new CountDownLatch(1);
        CountDownLatch  doneLatch  = new CountDownLatch(THREADS);

        AtomicInteger          successes         = new AtomicInteger(0);
        AtomicInteger          insufficientCredit = new AtomicInteger(0);
        List<Throwable>        unexpected         = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < THREADS; i++) {
            final String externalRef = "ORD-CONC-" + i;
            executor.submit(() -> {
                try {
                    TenantContext.setTenant(TENANT);
                    startLatch.await();  // todos os threads prontos, disparam juntos
                    orderService.createOrder(buildRequest(externalRef), null);
                    successes.incrementAndGet();
                } catch (InsufficientCreditException e) {
                    // Esperado: crédito esgotado; libera o lock que findByIdForUpdate adquiriu
                    insufficientCredit.incrementAndGet();
                    if (rowLock.isHeldByCurrentThread()) rowLock.unlock();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Throwable e) {
                    unexpected.add(e);
                    if (rowLock.isHeldByCurrentThread()) rowLock.unlock();
                } finally {
                    TenantContext.clear();
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();  // dispara todas as threads ao mesmo tempo
        boolean finished = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(finished).as("Todos os threads devem terminar em 30s").isTrue();
        assertThat(unexpected).as("Nenhuma exceção inesperada").isEmpty();

        // Invariante principal: crédito NUNCA vai abaixo de zero
        assertThat(dbCredit.get())
                .as("Crédito final nunca negativo")
                .isGreaterThanOrEqualTo(BigDecimal.ZERO);

        // Exatamente CREDIT_ORDERS pedidos aprovados
        assertThat(successes.get())
                .as("Apenas 3 pedidos aprovados (crédito suficiente para 3)")
                .isEqualTo(CREDIT_ORDERS);

        // Os 7 restantes falharam por crédito insuficiente
        assertThat(insufficientCredit.get())
                .as("7 pedidos rejeitados por crédito insuficiente")
                .isEqualTo(THREADS - CREDIT_ORDERS);

        // Crédito foi consumido totalmente
        assertThat(dbCredit.get())
                .as("Crédito esgotado após 3 pedidos aprovados")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void concurrentOrdersSameBuyer_totalApprovedValueDoesNotExceedInitialCredit() throws Exception {
        BigDecimal initialCredit = ORDER_AMOUNT.multiply(BigDecimal.valueOf(CREDIT_ORDERS));

        ExecutorService executor   = Executors.newFixedThreadPool(THREADS);
        CountDownLatch  startLatch = new CountDownLatch(1);
        CountDownLatch  doneLatch  = new CountDownLatch(THREADS);
        AtomicInteger   successes  = new AtomicInteger(0);
        List<Throwable> unexpected = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < THREADS; i++) {
            final String ref = "ORD-VAL-" + i;
            executor.submit(() -> {
                try {
                    TenantContext.setTenant(TENANT);
                    startLatch.await();
                    orderService.createOrder(buildRequest(ref), null);
                    successes.incrementAndGet();
                } catch (InsufficientCreditException e) {
                    if (rowLock.isHeldByCurrentThread()) rowLock.unlock();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Throwable e) {
                    unexpected.add(e);
                    if (rowLock.isHeldByCurrentThread()) rowLock.unlock();
                } finally {
                    TenantContext.clear();
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(unexpected).isEmpty();

        BigDecimal totalApproved = ORDER_AMOUNT.multiply(BigDecimal.valueOf(successes.get()));
        assertThat(totalApproved)
                .as("Valor total aprovado não ultrapassa o crédito inicial")
                .isLessThanOrEqualTo(initialCredit);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Buyer buildBuyer(UUID id, BigDecimal credit) {
        Buyer b = new Buyer();
        ReflectionTestUtils.setField(b, "id", id);
        b.setExternalReference("BUYER-001");
        b.setName("Farmácia Central Ltda");
        b.setCreditLimit(credit);
        b.setTenantCode(TENANT);
        b.setEnabled(true);
        return b;
    }

    private Seller buildSeller() {
        Seller s = new Seller();
        ReflectionTestUtils.setField(s, "id", UUID.randomUUID());
        s.setExternalReference("SELLER-001");
        s.setName("Distribuidora Norte");
        s.setTenantCode(TENANT);
        return s;
    }

    private Warehouse buildWarehouse() {
        Warehouse w = new Warehouse();
        ReflectionTestUtils.setField(w, "id", UUID.randomUUID());
        w.setExternalReference("WH-001");
        w.setName("CD São Paulo");
        w.setTenantCode(TENANT);
        return w;
    }

    private PaymentCondition buildPaymentCondition() {
        PaymentCondition pc = new PaymentCondition();
        ReflectionTestUtils.setField(pc, "id", UUID.randomUUID());
        pc.setCode("30-DIAS");
        pc.setDescription("30 dias");
        pc.setMaxInstallments(1);
        pc.setDiscountPercentage(BigDecimal.ZERO);
        pc.setTenantCode(TENANT);
        return pc;
    }

    private ProductPrice buildProductPrice(Warehouse warehouse) {
        ProductPrice pp = new ProductPrice();
        pp.setProductCode("PROD-001");
        pp.setProductName("Dipirona 500mg");
        pp.setUnitPrice(ORDER_AMOUNT);  // 1 unidade × 100.00 = 100.00 por pedido
        pp.setListPrice(new BigDecimal("120.00"));
        pp.setWarehouse(warehouse);
        pp.setTenantCode(TENANT);
        return pp;
    }

    private CreateOrderRequest buildRequest(String externalRef) {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setExternalReference(externalRef);
        req.setBuyerReference("BUYER-001");
        req.setSellerReference("SELLER-001");
        req.setWarehouseReference("WH-001");
        req.setPaymentConditionCode("30-DIAS");
        req.setItems(List.of(new CreateOrderRequest.OrderItemRequest("PROD-001", 1)));
        return req;
    }
}