package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

public class InsufficientCreditException extends BusinessException {

    public InsufficientCreditException(BigDecimal available, BigDecimal required) {
        super(
                "ORD-VALIDATION-003",
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Limite de crédito insuficiente",
                List.of(
                        "Crédito disponível: R$ " + available,
                        "Valor do pedido: R$ " + required
                )
        );
    }
}