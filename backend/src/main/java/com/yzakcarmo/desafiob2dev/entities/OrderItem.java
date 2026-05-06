package com.yzakcarmo.desafiob2dev.entities;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {

	private UUID id;
	private UUID orderId;
	private String productCode;
	private String productName;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal listPrice;
	private BigDecimal subtotal;
}
