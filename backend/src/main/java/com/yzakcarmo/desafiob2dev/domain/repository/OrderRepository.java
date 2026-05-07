package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.Order;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByExternalReferenceAndTenantCode(String externalReference, String tenantCode);

    Optional<Order> findByExternalReferenceAndTenantCode(
            String externalReference, String tenantCode);

    // Query de listagem sem itens — evita N+1 na paginação
    // Os itens são carregados separadamente apenas quando necessário
    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.buyer b
            JOIN FETCH o.seller s
            JOIN FETCH o.warehouse w
            WHERE o.tenantCode = :tenantCode
              AND (:status IS NULL OR o.status = :status)
              AND (:buyerRef IS NULL OR b.externalReference = :buyerRef)
              AND (:dateFrom IS NULL OR o.createdAt >= :dateFrom)
              AND (:dateTo IS NULL OR o.createdAt <= :dateTo)
            """)
    Page<Order> findAllWithFilters(
            @Param("tenantCode") String tenantCode,
            @Param("status") OrderStatus status,
            @Param("buyerRef") String buyerRef,
            @Param("dateFrom") OffsetDateTime dateFrom,
            @Param("dateTo") OffsetDateTime dateTo,
            Pageable pageable);

    // Query separada para carregar itens de uma lista de pedidos — evita produto cartesiano com paginação
    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.items
            WHERE o.id IN :orderIds
            """)
    List<Order> findAllWithItemsByIds(@Param("orderIds") List<UUID> orderIds);

    // Busca completa para o endpoint de detalhes
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
}