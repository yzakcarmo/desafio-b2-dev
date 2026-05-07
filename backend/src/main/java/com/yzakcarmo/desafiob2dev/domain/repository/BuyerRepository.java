package com.yzakcarmo.desafiob2dev.domain.repository;

import com.yzakcarmo.desafiob2dev.domain.entity.Buyer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BuyerRepository extends JpaRepository<Buyer, UUID> {

    Optional<Buyer> findByExternalReferenceAndTenantCodeAndEnabledTrue(
            String externalReference, String tenantCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Buyer b WHERE b.id = :id AND b.tenantCode = :tenantCode")
    Optional<Buyer> findByIdForUpdate(@Param("id") UUID id, @Param("tenantCode") String tenantCode);
}