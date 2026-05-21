package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;

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
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RecuperarContrasenaRequest;
import com.example.demo.dto.ResetContrasenaRequest;
import com.example.demo.service.AdministradorService;
import com.example.demo.service.AuthService;
import com.example.demo.service.AuthService.LoginResult;
import com.example.demo.service.ClienteService;
import com.example.demo.service.OperadorService;
import com.example.demo.service.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthRestController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private AdministradorService administradorService;

    @MockitoBean
    private OperadorService operadorService;


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


    // ── POST /auth/recuperar-contrasena ───────────────────────────────────────

    @Test
    public void AuthController_recuperarContrasena_Retorna200CuandoEmailExiste() throws Exception {
        RecuperarContrasenaRequest req = new RecuperarContrasenaRequest();
        req.email = "pablo@x.com";
        doNothing().when(passwordResetService).solicitarRecuperacion("pablo@x.com");

        mockMvc.perform(post("/auth/recuperar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    public void AuthController_recuperarContrasena_Retorna404CuandoEmailNoExiste() throws Exception {
        RecuperarContrasenaRequest req = new RecuperarContrasenaRequest();
        req.email = "noexiste@x.com";
        doThrow(new NoSuchElementException("No existe ningún usuario con ese email."))
                .when(passwordResetService).solicitarRecuperacion("noexiste@x.com");

        mockMvc.perform(post("/auth/recuperar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe ningún usuario con ese email."));
    }

    @Test
    public void AuthController_recuperarContrasena_Retorna503CuandoServicioDeEmailFalla() throws Exception {
        RecuperarContrasenaRequest req = new RecuperarContrasenaRequest();
        req.email = "pablo@x.com";
        doThrow(new IllegalStateException("Servicio de email no disponible."))
                .when(passwordResetService).solicitarRecuperacion("pablo@x.com");

        mockMvc.perform(post("/auth/recuperar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Servicio de email no disponible."));
    }

    @Test
    public void AuthController_recuperarContrasena_Retorna500CuandoExcepcionInesperada() throws Exception {
        RecuperarContrasenaRequest req = new RecuperarContrasenaRequest();
        req.email = "pablo@x.com";
        doThrow(new RuntimeException("fallo SMTP"))
                .when(passwordResetService).solicitarRecuperacion("pablo@x.com");

        mockMvc.perform(post("/auth/recuperar-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("No se pudo enviar el correo. Intenta de nuevo."));
    }


    // ── POST /auth/reset-contrasena ───────────────────────────────────────────

    @Test
    public void AuthController_resetContrasena_Retorna200CuandoTokenYContrasenaValidos() throws Exception {
        ResetContrasenaRequest req = new ResetContrasenaRequest();
        req.token = "token-valido";
        req.nuevaContrasena = "nuevaPass123";
        doNothing().when(passwordResetService).resetContrasena("token-valido", "nuevaPass123");

        mockMvc.perform(post("/auth/reset-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    public void AuthController_resetContrasena_Retorna400CuandoArgumentoInvalido() throws Exception {
        ResetContrasenaRequest req = new ResetContrasenaRequest();
        req.token = "token-valido";
        req.nuevaContrasena = "";
        doThrow(new IllegalArgumentException("La contraseña no puede estar vacía."))
                .when(passwordResetService).resetContrasena(anyString(), anyString());

        mockMvc.perform(post("/auth/reset-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La contraseña no puede estar vacía."));
    }

    @Test
    public void AuthController_resetContrasena_Retorna404CuandoTokenNoExiste() throws Exception {
        ResetContrasenaRequest req = new ResetContrasenaRequest();
        req.token = "token-inexistente";
        req.nuevaContrasena = "nuevaPass123";
        doThrow(new NoSuchElementException("Token inválido o expirado."))
                .when(passwordResetService).resetContrasena(anyString(), anyString());

        mockMvc.perform(post("/auth/reset-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Token inválido o expirado."));
    }

    @Test
    public void AuthController_resetContrasena_Retorna500CuandoExcepcionInesperada() throws Exception {
        ResetContrasenaRequest req = new ResetContrasenaRequest();
        req.token = "token-valido";
        req.nuevaContrasena = "nuevaPass123";
        doThrow(new RuntimeException("fallo interno"))
                .when(passwordResetService).resetContrasena(anyString(), anyString());

        mockMvc.perform(post("/auth/reset-contrasena")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("No se pudo actualizar la contraseña. Intenta de nuevo."));
    }
}
