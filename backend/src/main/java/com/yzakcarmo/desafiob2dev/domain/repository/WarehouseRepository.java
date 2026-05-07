package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByExternalReferenceAndTenantCodeAndEnabledTrue(
            String externalReference, String tenantCode);
}