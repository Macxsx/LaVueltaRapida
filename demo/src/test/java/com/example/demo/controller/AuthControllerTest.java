package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.LoginRequest;
import com.example.demo.service.AuthService;
import com.example.demo.service.AuthService.LoginResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthRestController.class)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;


    private LoginRequest buildRequest(String usuario, String contrasena) {
        LoginRequest req = new LoginRequest();
        req.usuario = usuario;
        req.contrasena = contrasena;
        return req;
    }


    @Test
    public void AuthController_login_RetornaLoginResponseCuandoAdministradorEsValido() throws Exception {
        LoginRequest req = buildRequest("admin1", "123");
        when(authService.login(eq("admin1"), eq("123")))
                .thenReturn(new LoginResult("admin1", "ADMIN"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("admin1"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    public void AuthController_login_RetornaLoginResponseConClienteIdYCarritoIdCuandoEsCliente() throws Exception {
        LoginRequest req = buildRequest("pablo123", "123456");
        when(authService.login(eq("pablo123"), eq("123456")))
                .thenReturn(new LoginResult("pablo123", "CLIENTE", 7L, 42L));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("pablo123"))
                .andExpect(jsonPath("$.role").value("CLIENTE"))
                .andExpect(jsonPath("$.clienteId").value(7))
                .andExpect(jsonPath("$.carritoId").value(42));
    }

    @Test
    public void AuthController_login_Retorna400CuandoArgumentoInvalido() throws Exception {
        LoginRequest req = buildRequest("", "");
        when(authService.login(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Usuario y contraseña son obligatorios."));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Usuario y contraseña son obligatorios."));
    }

    @Test
    public void AuthController_login_Retorna403CuandoCuentaDesactivada() throws Exception {
        LoginRequest req = buildRequest("pablo123", "123456");
        when(authService.login(anyString(), anyString()))
                .thenThrow(new IllegalStateException("La cuenta de cliente está desactivada."));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("La cuenta de cliente está desactivada."));
    }

    @Test
    public void AuthController_login_Retorna401CuandoCredencialesInvalidas() throws Exception {
        LoginRequest req = buildRequest("admin1", "incorrecta");
        when(authService.login(anyString(), anyString()))
                .thenThrow(new SecurityException("Credenciales inválidas."));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
