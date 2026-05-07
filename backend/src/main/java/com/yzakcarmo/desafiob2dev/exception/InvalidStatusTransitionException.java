package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;

public class InvalidStatusTransitionException extends BusinessException {

    public InvalidStatusTransitionException(String currentStatus) {
        super(
                "ORD-STATUS-001",
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Transição de status inválida. Status atual não permite cancelamento: " + currentStatus
        );
    }
}