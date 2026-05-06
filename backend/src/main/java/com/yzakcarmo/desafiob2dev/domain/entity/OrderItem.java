package com.yzakcarmo.desafiob2dev.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_item")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Column(name = "product_code", nullable = false, length = 100)
	private String productCode;

	@Column(name = "product_name", nullable = false, length = 255)
	private String productName;

	@Column(nullable = false)
	private Integer quantity;

	@Column(name = "unit_price", nullable = false, precision = 15, scale = 4)
	private BigDecimal unitPrice;

	@Column(name = "list_price", nullable = false, precision = 15, scale = 4)
	private BigDecimal listPrice;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal subtotal;

	public UUID getId() { return id; }
	public Order getOrder() { return order; }
	public void setOrder(Order order) { this.order = order; }
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