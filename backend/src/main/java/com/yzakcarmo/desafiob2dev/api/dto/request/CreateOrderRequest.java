package com.yzakcarmo.desafiob2dev.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class CreateOrderRequest {

    @NotBlank(message = "externalReference é obrigatório")
    private String externalReference;

    @NotBlank(message = "buyerReference é obrigatório")
    private String buyerReference;

    @NotBlank(message = "sellerReference é obrigatório")
    private String sellerReference;

    @NotBlank(message = "warehouseReference é obrigatório")
    private String warehouseReference;

    @NotBlank(message = "paymentConditionCode é obrigatório")
    private String paymentConditionCode;

    @NotEmpty(message = "items não pode ser vazio")
    @Valid
    private List<OrderItemRequest> items;

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
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public record OrderItemRequest(
            @NotBlank(message = "productCode é obrigatório") String productCode,
            @NotNull @Positive(message = "quantity deve ser maior que zero") Integer quantity
    ) {}
}