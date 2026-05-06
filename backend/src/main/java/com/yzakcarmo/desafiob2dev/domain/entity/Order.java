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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public UUID getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(UUID buyerId) {
		this.buyerId = buyerId;
	}

	public UUID getSellerId() {
		return sellerId;
	}

	public void setSellerId(UUID sellerId) {
		this.sellerId = sellerId;
	}

	public UUID getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(UUID warehouseId) {
		this.warehouseId = warehouseId;
	}

	public UUID getPaymentConditionId() {
		return paymentConditionId;
	}

	public void setPaymentConditionId(UUID paymentConditionId) {
		this.paymentConditionId = paymentConditionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
