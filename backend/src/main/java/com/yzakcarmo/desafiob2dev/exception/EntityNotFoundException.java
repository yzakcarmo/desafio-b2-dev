package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityName, String reference) {
        super(
                "ORD-VALIDATION-002",
                HttpStatus.UNPROCESSABLE_ENTITY,
                entityName + " não encontrado(a): " + reference
        );
    }
}