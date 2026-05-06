package com.yzakcarmo.desafiob2dev.domain.entity;

import com.yzakcarmo.desafiob2dev.domain.enums.OrderOrigin;
import com.yzakcarmo.desafiob2dev.domain.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"order\"")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "external_reference", nullable = false, unique = true, length = 100)
	private String externalReference;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buyer_id", nullable = false)
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private Seller seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_condition_id", nullable = false)
	private PaymentCondition paymentCondition;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OrderStatus status = OrderStatus.PENDING;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "discount_value", precision = 15, scale = 2)
	private BigDecimal discountValue = BigDecimal.ZERO;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OrderOrigin origin = OrderOrigin.API;

	@Column(name = "tenant_code", nullable = false, length = 50)
	private String tenantCode;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@Column(name = "last_modified")
	private OffsetDateTime lastModified;

	@Version
	@Column(nullable = false)
	private Long version;

	@OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.createdAt = OffsetDateTime.now();
		this.lastModified = OffsetDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.lastModified = OffsetDateTime.now();
	}

	public void addItem(OrderItem item) {
		item.setOrder(this);
		this.items.add(item);
	}

	public UUID getId() { return id; }
	public String getExternalReference() { return externalReference; }
	public void setExternalReference(String externalReference) { this.externalReference = externalReference; }
	public Buyer getBuyer() { return buyer; }
	public void setBuyer(Buyer buyer) { this.buyer = buyer; }
	public Seller getSeller() { return seller; }
	public void setSeller(Seller seller) { this.seller = seller; }
	public Warehouse getWarehouse() { return warehouse; }
	public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
	public PaymentCondition getPaymentCondition() { return paymentCondition; }
	public void setPaymentCondition(PaymentCondition paymentCondition) { this.paymentCondition = paymentCondition; }
	public OrderStatus getStatus() { return status; }
	public void setStatus(OrderStatus status) { this.status = status; }
	public BigDecimal getSubtotal() { return subtotal; }
	public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
	public BigDecimal getDiscountValue() { return discountValue; }
	public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
	public BigDecimal getTotal() { return total; }
	public void setTotal(BigDecimal total) { this.total = total; }
	public OrderOrigin getOrigin() { return origin; }
	public void setOrigin(OrderOrigin origin) { this.origin = origin; }
	public String getTenantCode() { return tenantCode; }
	public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public OffsetDateTime getLastModified() { return lastModified; }
	public Long getVersion() { return version; }
	public List<OrderItem> getItems() { return items; }
}