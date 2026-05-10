package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.ProductPrice;
import com.yzakcarmo.desafiob2dev.domain.repository.projection.ListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, UUID> {

    @Query("""
            SELECT pp FROM ProductPrice pp
            WHERE pp.productCode IN :productCodes
              AND pp.warehouse.id = :warehouseId
              AND pp.tenantCode = :tenantCode
              AND pp.enabled = true
            """)
    List<ProductPrice> findByProductCodesAndWarehouse(
            @Param("productCodes") List<String> productCodes,
            @Param("warehouseId") UUID warehouseId,
            @Param("tenantCode") String tenantCode);

    @Query("""
        SELECT p.productCode AS externalReference,
               p.productName AS name
        FROM ProductPrice p
        WHERE p.warehouse.id = :warehouseId AND p.enabled = true
        """)
    List<ListProjection> listAllToOrder(@Param("warehouseId") UUID warehouseId);
}