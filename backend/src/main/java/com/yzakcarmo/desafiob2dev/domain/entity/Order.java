package com.yzakcarmo.desafiob2dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"order\"")
public class Order {

	@Id
	private UUID id;

	@Column(name = "external_reference", unique = true, nullable = false)
	private String externalReference;

	@Column(name = "buyer_id", nullable = false)
	private UUID buyerId;

	@Column(name = "seller_id", nullable = false)
	private UUID sellerId;

	@Column(name = "warehouse_id", nullable = false)
	private UUID warehouseId;

	@Column(name = "payment_condition_id", nullable = false)
	private UUID paymentConditionId;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "subtotal", nullable = false)
	private BigDecimal subtotal;

	@Column(name = "discount_value")
	private BigDecimal discountValue;

	@Column(name = "total", nullable = false)
	private BigDecimal total;

	@Column(name = "origin", nullable = false)
	private String origin;

	@Column(name = "tenant_code", nullable = false)
	private String tenantCode;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "last_modified")
	private LocalDateTime lastModified;

	@Version
	private Long version;
}
