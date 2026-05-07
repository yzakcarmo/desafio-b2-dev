package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class TenantValidationException extends BusinessException {

    public TenantValidationException(List<String> errors) {
        super(
                "ORD-VALIDATION-005",
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Falha na validação da strategy do tenant",
                errors
        );
    }
}