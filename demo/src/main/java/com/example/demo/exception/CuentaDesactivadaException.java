package com.example.demo.exception;

public class CuentaDesactivadaException extends RuntimeException {

    public CuentaDesactivadaException(String mensaje) {
        super(mensaje);
    }
}
