package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    public String token;
    public String username;
    public String role;
    public Long clienteId;
    public Long carritoId;

    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public LoginResponse(String token, String username, String role, Long clienteId, Long carritoId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.clienteId = clienteId;
        this.carritoId = carritoId;
    }
}
