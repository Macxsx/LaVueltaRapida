package com.example.demo.service;

import java.util.Map;

public interface AuthService {

    /**
     * Realiza el login de un usuario (administrador, operador o cliente)
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña
     * @return Map con {"success": true, "username": String, "role": String, "clienteId": Long, "carritoId": Long}
     *         o {"success": false, "error": String}
     */
    Map<String, Object> login(String usuario, String contrasena);
}
