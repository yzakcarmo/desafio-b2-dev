package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;

public class DuplicateExternalReferenceException extends BusinessException {

    public DuplicateExternalReferenceException(String externalReference) {
        super(
                "ORD-DUPLICATE-001",
                HttpStatus.CONFLICT,
                "External reference já existe: " + externalReference
        );
    }
}