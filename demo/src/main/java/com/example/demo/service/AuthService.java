package com.example.demo.service;

public interface AuthService {

    /**
     * Lanza SecurityException si las credenciales son inválidas.
     * Lanza IllegalStateException si la cuenta de cliente está desactivada.
     */
    LoginResult login(String usuario, String contrasena);

    class LoginResult {
        public final String username;
        public final String role;
        public final Long clienteId;
        public final Long carritoId;

        public LoginResult(String username, String role) {
            this.username = username;
            this.role = role;
            this.clienteId = null;
            this.carritoId = null;
        }

        public LoginResult(String username, String role, Long clienteId, Long carritoId) {
            this.username = username;
            this.role = role;
            this.clienteId = clienteId;
            this.carritoId = carritoId;
        }
    }
}
