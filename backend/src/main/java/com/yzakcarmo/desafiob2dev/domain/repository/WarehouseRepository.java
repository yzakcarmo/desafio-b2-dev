package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.Warehouse;
import com.yzakcarmo.desafiob2dev.domain.repository.projection.ListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByExternalReferenceAndTenantCodeAndEnabledTrue(
            String externalReference, String tenantCode);

    @Query("""
        SELECT w.externalReference AS externalReference,
               w.name AS name,
               w.id as id
        FROM Warehouse w
        WHERE w.seller.id = :sellerId AND w.enabled = true
        """)
    List<ListProjection> listAllToOrder(@Param("sellerId") UUID sellerId);
}