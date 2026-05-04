package com.example.demo.service;

public interface PasswordResetService {
    void solicitarRecuperacion(String email);
    void resetContrasena(String token, String nuevaContrasena);
}
