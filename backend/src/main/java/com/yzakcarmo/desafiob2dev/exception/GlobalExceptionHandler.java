package com.yzakcarmo.desafiob2dev.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponse resp = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "ORD-VALIDATION-001",
                "Campos inválidos ou ausentes", details, null);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resp);
    }

    @ExceptionHandler(OrderBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(OrderBusinessException ex) {
        ErrorResponse resp = new ErrorResponse(ex.getStatus().value(), ex.getCode(), ex.getMessage(), ex.getDetails(), null);
        return ResponseEntity.status(ex.getStatus()).body(resp);
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleConcurrency(Exception ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.CONFLICT.value(), "ORD-CONCURRENCY-001",
                "Conflito de concorrência", Collections.singletonList(ex.getMessage()), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DataIntegrityViolationException ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.CONFLICT.value(), "ORD-DUPLICATE-001",
                "External reference duplicado", Collections.singletonList(ex.getMostSpecificCause().getMessage()), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
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
