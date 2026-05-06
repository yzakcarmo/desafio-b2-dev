package com.yzakcarmo.desafiob2dev.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Order {

	private UUID id;
	private String externalReference;
	private UUID buyerId;
	private UUID sellerId;
	private UUID warehouseId;
	private UUID paymentConditionId;
	private String status;
	private BigDecimal subtotal;
	private BigDecimal discountValue;
	private BigDecimal total;
	private String origin;
	private String tenantCode;
	private LocalDateTime createdAt;
	private LocalDateTime lastModified;
	private Long version;
}
