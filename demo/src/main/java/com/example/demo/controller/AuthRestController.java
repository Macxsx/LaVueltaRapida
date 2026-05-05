package com.example.demo.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RecuperarContrasenaRequest;
import com.example.demo.dto.ResetContrasenaRequest;
import com.example.demo.error.ApiError;
import com.example.demo.service.AuthService;
import com.example.demo.service.AuthService.LoginResult;
import com.example.demo.service.PasswordResetService;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResult result = authService.login(request.usuario, request.contrasena);
            return ResponseEntity.ok(new LoginResponse(result.username, result.role, result.clienteId, result.carritoId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiError.of(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError.of(e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<?> recuperarContrasena(@RequestBody RecuperarContrasenaRequest request) {
        try {
            passwordResetService.solicitarRecuperacion(request.email);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ApiError.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.of("No se pudo enviar el correo. Intenta de nuevo."));
        }
    }

    @PostMapping("/reset-contrasena")
    public ResponseEntity<?> resetContrasena(@RequestBody ResetContrasenaRequest request) {
        try {
            passwordResetService.resetContrasena(request.token, request.nuevaContrasena);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiError.of(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.of("No se pudo actualizar la contraseña. Intenta de nuevo."));
        }
    }
}
