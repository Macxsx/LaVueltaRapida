package com.example.demo.entitys;

public enum Role {
    ADMIN, OPERADOR, CLIENTE;

    public String authority() {
        return "ROLE_" + name();
    }

    public String value() {
        return name().toLowerCase();
    }
}
