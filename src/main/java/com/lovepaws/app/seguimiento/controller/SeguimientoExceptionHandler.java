package com.lovepaws.app.seguimiento.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lovepaws.app.seguimiento.exception.InvalidSeguimientoStateException;
import com.lovepaws.app.seguimiento.exception.SeguimientoForbiddenException;
import com.lovepaws.app.seguimiento.exception.SeguimientoNotFoundException;

@RestControllerAdvice(basePackages = "com.lovepaws.app.seguimiento")
public class SeguimientoExceptionHandler {

    @ExceptionHandler(SeguimientoNotFoundException.class)
    public ResponseEntity<?> handleNotFound(SeguimientoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "NOT_FOUND",
                        "message", ex.getMessage()));
    }

    @ExceptionHandler(SeguimientoForbiddenException.class)
    public ResponseEntity<?> handleForbidden(SeguimientoForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "FORBIDDEN",
                        "message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidSeguimientoStateException.class)
    public ResponseEntity<?> handleInvalidState(InvalidSeguimientoStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", "INVALID_STATE",
                        "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(err -> err instanceof FieldError fe ? fe.getDefaultMessage() : err.getDefaultMessage())
                .orElse("Solicitud inválida");
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "error", "VALIDATION_ERROR",
                "message", message));
    }
}
