package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;

public class ConcurrencyException extends BusinessException {

    public ConcurrencyException() {
        super(
                "ORD-CONCURRENCY-001",
                HttpStatus.CONFLICT,
                "Conflito de concorrência detectado. Tente novamente."
        );
    }
}