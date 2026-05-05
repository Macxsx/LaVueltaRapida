package com.example.demo.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CuentaDesactivadaException.class)
    public ResponseEntity<?> handleCuentaDesactivada(CuentaDesactivadaException e) {
        return ResponseEntity.ok(Map.of("mensaje", e.getMessage()));
    }
}
