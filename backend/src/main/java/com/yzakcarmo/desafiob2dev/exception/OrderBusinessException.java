package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class OrderBusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final List<String> details;

    public OrderBusinessException(HttpStatus status, String code, String message, List<String> details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public List<String> getDetails() {
        return details;
    }
}

