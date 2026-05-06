package com.yzakcarmo.desafiob2dev.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_item")
public class OrderItem {

	@Id
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Column(name = "product_code", nullable = false)
	private String productCode;

	@Column(name = "product_name", nullable = false)
	private String productName;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "unit_price", nullable = false)
	private BigDecimal unitPrice;

	@Column(name = "list_price", nullable = false)
	private BigDecimal listPrice;

	@Column(name = "subtotal", nullable = false)
	private BigDecimal subtotal;

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public UUID getOrderId() { return orderId; }
	public void setOrderId(UUID orderId) { this.orderId = orderId; }

	public String getProductCode() { return productCode; }
	public void setProductCode(String productCode) { this.productCode = productCode; }

	public String getProductName() { return productName; }
	public void setProductName(String productName) { this.productName = productName; }

	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }

	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

	public BigDecimal getListPrice() { return listPrice; }
	public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

	public BigDecimal getSubtotal() { return subtotal; }
	public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
