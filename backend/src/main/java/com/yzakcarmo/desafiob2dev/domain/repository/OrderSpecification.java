package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.Buyer;
import com.yzakcarmo.desafiob2dev.domain.entity.Order;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class OrderSpecification {

    private OrderSpecification() {}

    public static Specification<Order> withFilters(
            String tenantCode,
            OrderStatus status,
            String buyerRef,
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo) {

        return (root, query, cb) -> {

            // Força JOIN FETCH apenas em queries de dados, não de count
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("buyer", JoinType.INNER);
                root.fetch("seller", JoinType.INNER);
                root.fetch("warehouse", JoinType.INNER);
                query.distinct(true);
            }

            Predicate predicate = cb.equal(root.get("tenantCode"), tenantCode);

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }

            if (buyerRef != null && !buyerRef.isBlank()) {
                Join<Order, Buyer> buyer = root.join("buyer", JoinType.INNER);
                predicate = cb.and(predicate,
                        cb.equal(buyer.get("externalReference"), buyerRef));
            }

            if (dateFrom != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
            }

            if (dateTo != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("createdAt"), dateTo));
            }

            return predicate;
        };
    }
}