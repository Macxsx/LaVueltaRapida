package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
import com.example.demo.dto.UpdateAdminRequest;
import com.example.demo.entitys.Administrador;
import com.example.demo.service.AdministradorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AdministradorRestController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
public class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdministradorService administradorService;

    @MockitoBean
    private JwtService jwtService;


    private UpdateAdminRequest buildRequest(String usuario, String contrasena, String currentPassword) {
        UpdateAdminRequest req = new UpdateAdminRequest();
        req.usuario = usuario;
        req.contrasena = contrasena;
        req.currentPassword = currentPassword;
        return req;
    }


    @Test
    public void AdministradorController_findAll_RetornaColeccionDeAdministradores() throws Exception {
        when(administradorService.findAll()).thenReturn(List.of(
                new Administrador(1L, "admin1", "123"),
                new Administrador(2L, "admin2", "123")));

        mockMvc.perform(get("/administradores"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].usuario").value("admin1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].usuario").value("admin2"));
    }

    @Test
    public void AdministradorController_findAll_RetornaListaVaciaCuandoNoHayAdministradores() throws Exception {
        when(administradorService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/administradores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void AdministradorController_findById_RetornaAdministradorCuandoExiste() throws Exception {
        when(administradorService.findById(1L))
                .thenReturn(Optional.of(new Administrador(1L, "admin1", "123")));

        mockMvc.perform(get("/administradores/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("admin1"));
    }

    @Test
    public void AdministradorController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(administradorService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/administradores/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void AdministradorController_update_ActualizaYRetorna200() throws Exception {
        UpdateAdminRequest req = buildRequest("admin1Nuevo", "456", "123");
        when(administradorService.update(eq(1L), eq("admin1Nuevo"), eq("456"), eq("123")))
                .thenReturn(new Administrador(1L, "admin1Nuevo", "456"));

        mockMvc.perform(put("/administradores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("admin1Nuevo"));
    }

    @Test
    public void AdministradorController_update_Retorna404CuandoNoExiste() throws Exception {
        UpdateAdminRequest req = buildRequest("admin1Nuevo", "456", "123");
        when(administradorService.update(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(new NoSuchElementException("Administrador no encontrado."));

        mockMvc.perform(put("/administradores/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void AdministradorController_update_Retorna401CuandoCurrentPasswordIncorrecto() throws Exception {
        UpdateAdminRequest req = buildRequest("admin1Nuevo", "456", "incorrecta");
        when(administradorService.update(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(new SecurityException("Contraseña actual incorrecta."));

        mockMvc.perform(put("/administradores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Contraseña actual incorrecta."));
    }

    @Test
    public void AdministradorController_update_Retorna409CuandoUsuarioYaExiste() throws Exception {
        UpdateAdminRequest req = buildRequest("admin2", "456", "123");
        when(administradorService.update(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalStateException("El usuario ya pertenece a otro administrador."));

        mockMvc.perform(put("/administradores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El usuario ya pertenece a otro administrador."));
    }


    @Test
    public void AdministradorController_delete_Retorna204CuandoSeElimina() throws Exception {
        doNothing().when(administradorService).delete(1L);

        mockMvc.perform(delete("/administradores/1"))
                .andExpect(status().isNoContent());

        verify(administradorService).delete(1L);
    }

    @Test
    public void AdministradorController_delete_Retorna404CuandoNoExiste() throws Exception {
        doThrow(new NoSuchElementException("Administrador no encontrado."))
                .when(administradorService).delete(999L);

        mockMvc.perform(delete("/administradores/999"))
                .andExpect(status().isNotFound());
    }
}
