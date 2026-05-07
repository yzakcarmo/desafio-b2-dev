package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;

public class ProductPriceNotFoundException extends BusinessException {

    public ProductPriceNotFoundException(String productCode) {
        super(
                "ORD-VALIDATION-004",
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Produto não encontrado na tabela de preços para o warehouse informado: " + productCode
        );
    }
}