package com.lovepaws.app.seguimiento.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lovepaws.app.seguimiento.exception.EstadoInvalidoException;
import com.lovepaws.app.seguimiento.exception.SeguimientoException;

@RestControllerAdvice(basePackages = "com.lovepaws.app.seguimiento")
public class SeguimientoControllerAdvice {

    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<?> handleEstado(EstadoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("timestamp", LocalDateTime.now(), "error", "ESTADO_INVALIDO", "message", ex.getMessage()));
    }

    @ExceptionHandler(SeguimientoException.class)
    public ResponseEntity<?> handleSeguimiento(SeguimientoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("timestamp", LocalDateTime.now(), "error", "SEGUIMIENTO_ERROR", "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(e -> e instanceof FieldError fe ? fe.getDefaultMessage() : e.getDefaultMessage())
                .orElse("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("timestamp", LocalDateTime.now(), "error", "VALIDATION", "message", msg));
    }
}
