package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "ORD-VALIDATION-001",
                "Campos inválidos ou ausentes", Collections.singletonList(ex.getMessage()), null);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resp);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnsupported(UnsupportedOperationException ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.NOT_IMPLEMENTED.value(), "GEN-NOT-IMPLEMENTED",
                ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(resp);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "GEN-ERROR",
                "Erro interno", Collections.singletonList(ex.getMessage()), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
