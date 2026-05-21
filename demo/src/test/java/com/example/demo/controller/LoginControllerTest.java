package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.config.JwtService;
import com.example.demo.dto.ClienteLoginRequest;
import com.example.demo.entitys.Cliente;
import com.example.demo.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = LoginRestController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private JwtService jwtService;


    private Cliente buildCliente(Long id, String username, String email) {
        Cliente c = new Cliente("Pablo", "Pérez", email, username, "123456", "Cra 1 #2-3", "3001234567");
        c.setId(id);
        return c;
    }

    private ClienteLoginRequest buildRequest(String username, String password) {
        ClienteLoginRequest req = new ClienteLoginRequest();
        req.username = username;
        req.password = password;
        return req;
    }


    @Test
    public void LoginController_login_RetornaClienteCuandoCredencialesSonValidas() throws Exception {
        ClienteLoginRequest req = buildRequest("pablo123", "123456");
        when(clienteService.loginCliente(eq("pablo123"), eq("123456")))
                .thenReturn(buildCliente(1L, "pablo123", "pablo@x.com"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("pablo123"))
                .andExpect(jsonPath("$.email").value("pablo@x.com"));
    }

    @Test
    public void LoginController_login_Retorna400CuandoArgumentoInvalido() throws Exception {
        ClienteLoginRequest req = buildRequest(null, "123456");
        when(clienteService.loginCliente(any(), any()))
                .thenThrow(new IllegalArgumentException("El username y la contraseña son obligatorios."));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void LoginController_login_Retorna401CuandoCredencialesIncorrectas() throws Exception {
        ClienteLoginRequest req = buildRequest("pablo123", "incorrecta");
        when(clienteService.loginCliente(anyString(), anyString()))
                .thenThrow(new SecurityException("Credenciales incorrectas."));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void LoginController_login_Retorna401CuandoCuentaDesactivada() throws Exception {
        ClienteLoginRequest req = buildRequest("pablo123", "123456");
        when(clienteService.loginCliente(anyString(), anyString()))
                .thenThrow(new IllegalStateException("La cuenta está desactivada."));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
