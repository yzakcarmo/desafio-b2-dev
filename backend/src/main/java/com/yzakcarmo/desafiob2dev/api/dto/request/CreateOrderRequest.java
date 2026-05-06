package com.yzakcarmo.desafiob2dev.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateOrderRequest {

    @NotBlank
    private String externalReference;

    @NotBlank
    private String buyerReference;

    @NotBlank
    private String sellerReference;

    @NotBlank
    private String warehouseReference;

    @NotBlank
    private String paymentConditionCode;

    @NotEmpty
    private List<CreateOrderItemRequest> items;

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

    public String getBuyerReference() { return buyerReference; }
    public void setBuyerReference(String buyerReference) { this.buyerReference = buyerReference; }

    public String getSellerReference() { return sellerReference; }
    public void setSellerReference(String sellerReference) { this.sellerReference = sellerReference; }

    public String getWarehouseReference() { return warehouseReference; }
    public void setWarehouseReference(String warehouseReference) { this.warehouseReference = warehouseReference; }

    public String getPaymentConditionCode() { return paymentConditionCode; }
    public void setPaymentConditionCode(String paymentConditionCode) { this.paymentConditionCode = paymentConditionCode; }

    public List<CreateOrderItemRequest> getItems() { return items; }
    public void setItems(List<CreateOrderItemRequest> items) { this.items = items; }
}
