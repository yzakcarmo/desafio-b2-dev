package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.Order;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderStatus;
import com.yzakcarmo.desafiob2dev.domain.repository.projection.TopBuyerProjection;
import com.yzakcarmo.desafiob2dev.domain.repository.projection.TopProductProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    boolean existsByExternalReferenceAndTenantCode(String externalReference, String tenantCode);

    Optional<Order> findByExternalReferenceAndTenantCode(
            String externalReference, String tenantCode);

    // Query separada para carregar itens de uma lista de pedidos — evita produto cartesiano com paginação
    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.items
            WHERE o.id IN :orderIds
            """)
    List<Order> findAllWithItemsByIds(@Param("orderIds") List<UUID> orderIds);

    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.buyer
            JOIN FETCH o.seller
            JOIN FETCH o.warehouse
            JOIN FETCH o.paymentCondition
            JOIN FETCH o.items
            WHERE o.externalReference = :externalReference
              AND o.tenantCode = :tenantCode
            """)
    Optional<Order> findDetailByExternalReference(
            @Param("externalReference") String externalReference,
            @Param("tenantCode") String tenantCode);

    @Query("""
        SELECT COUNT(o) FROM Order o
        WHERE o.tenantCode = :tenantCode
          AND o.status = :status
          AND o.createdAt BETWEEN :dateFrom AND :dateTo
        """)
    long countByStatus(@Param("tenantCode") String tenantCode,
                       @Param("status") OrderStatus status,
                       @Param("dateFrom") OffsetDateTime dateFrom,
                       @Param("dateTo") OffsetDateTime dateTo);

    @Query("""
        SELECT COALESCE(SUM(o.total), 0) FROM Order o
        WHERE o.tenantCode = :tenantCode
          AND o.status = 'CONFIRMED'
          AND o.createdAt BETWEEN :dateFrom AND :dateTo
        """)
    BigDecimal sumRevenue(@Param("tenantCode") String tenantCode,
                          @Param("dateFrom") OffsetDateTime dateFrom,
                          @Param("dateTo") OffsetDateTime dateTo);

    @Query("""
        SELECT COALESCE(AVG(o.total), 0) FROM Order o
        WHERE o.tenantCode = :tenantCode
          AND o.status = 'CONFIRMED'
          AND o.createdAt BETWEEN :dateFrom AND :dateTo
        """)
    BigDecimal avgOrderValue(@Param("tenantCode") String tenantCode,
                             @Param("dateFrom") OffsetDateTime dateFrom,
                             @Param("dateTo") OffsetDateTime dateTo);

    // Top 5 buyers por receita
    @Query("""
        SELECT b.name AS name,
               COUNT(o) AS orderCount,
               SUM(o.total) AS totalSpent
        FROM Order o
        JOIN o.buyer b
        WHERE o.tenantCode = :tenantCode
          AND o.status = Status
          AND o.createdAt BETWEEN :dateFrom AND :dateTo
        GROUP BY b.id, b.name
        ORDER BY totalSpent DESC
        LIMIT 5
        """)
    List<TopBuyerProjection> findTopBuyers(@Param("tenantCode") String tenantCode,
                                           @Param("dateFrom") OffsetDateTime dateFrom,
                                           @Param("dateTo") OffsetDateTime dateTo);

    // Top 5 produtos por quantidade
    @Query("""
        SELECT i.productCode AS productCode,
               i.productName AS productName,
               SUM(i.quantity) AS totalQuantity
        FROM OrderItem i
        JOIN i.order o
        WHERE o.tenantCode = :tenantCode
          AND o.status = 'CONFIRMED'
          AND o.createdAt BETWEEN :dateFrom AND :dateTo
        GROUP BY i.productCode, i.productName
        ORDER BY totalQuantity DESC
        LIMIT 5
        """)
    List<TopProductProjection> findTopProducts(@Param("tenantCode") String tenantCode,
                                               @Param("dateFrom") OffsetDateTime dateFrom,
                                               @Param("dateTo") OffsetDateTime dateTo);
}