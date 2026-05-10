package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.Seller;
import com.yzakcarmo.desafiob2dev.domain.repository.projection.ListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {

    Optional<Seller> findByExternalReferenceAndTenantCodeAndEnabledTrue(
            String externalReference, String tenantCode);

    @Query("""
        SELECT s.externalReference AS externalReference,
               s.name AS name,
               s.id AS id
        FROM Seller s
        WHERE s.tenantCode = :tenant AND s.enabled = true
        """)
    List<ListProjection> listAllToOrder(@Param("tenant") String tenant);
}