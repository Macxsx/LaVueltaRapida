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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.example.demo.dto.UpdateOperadorRequest;
import com.example.demo.entitys.Operador;
import com.example.demo.service.OperadorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = OperadorRestController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
public class OperadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OperadorService operadorService;

    @MockitoBean
    private JwtService jwtService;


    private Operador buildOperador(Long id, String nombre, String usuario) {
        return new Operador(id, nombre, usuario, "123456");
    }

    private UpdateOperadorRequest buildUpdateRequest() {
        UpdateOperadorRequest req = new UpdateOperadorRequest();
        req.nombre = "OperadorEditado";
        req.usuario = "operador1";
        req.contrasena = "nueva";
        req.currentPassword = "123456";
        return req;
    }


    @Test
    public void OperadorController_findAll_RetornaColeccionDeOperadores() throws Exception {
        when(operadorService.findAll()).thenReturn(List.of(
                buildOperador(1L, "Carlos", "operador1"),
                buildOperador(2L, "Ana", "operador2")));

        mockMvc.perform(get("/operadores"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Carlos"))
                .andExpect(jsonPath("$[0].usuario").value("operador1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].usuario").value("operador2"));
    }

    @Test
    public void OperadorController_findAll_RetornaListaVaciaCuandoNoHayOperadores() throws Exception {
        when(operadorService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/operadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void OperadorController_findById_RetornaOperadorCuandoExiste() throws Exception {
        when(operadorService.findById(1L))
                .thenReturn(Optional.of(buildOperador(1L, "Carlos", "operador1")));

        mockMvc.perform(get("/operadores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.usuario").value("operador1"));
    }

    @Test
    public void OperadorController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(operadorService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/operadores/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void OperadorController_add_CreaOperadorYRetorna200() throws Exception {
        Operador nuevo = buildOperador(null, "Carlos", "operador1");
        when(operadorService.create(anyString(), anyString(), anyString()))
                .thenReturn(buildOperador(99L, "Carlos", "operador1"));

        mockMvc.perform(post("/operadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.usuario").value("operador1"));
    }

    @Test
    public void OperadorController_add_Retorna409CuandoUsuarioYaExiste() throws Exception {
        Operador nuevo = buildOperador(null, "Carlos", "operador1");
        when(operadorService.create(anyString(), anyString(), anyString()))
                .thenThrow(new IllegalStateException("El usuario ya está en uso."));

        mockMvc.perform(post("/operadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El usuario ya está en uso."));
    }


    @Test
    public void OperadorController_update_ActualizaYRetorna200() throws Exception {
        UpdateOperadorRequest req = buildUpdateRequest();
        when(operadorService.update(eq(1L), eq("OperadorEditado"), eq("operador1"),
                eq("nueva"), eq("123456")))
                .thenReturn(buildOperador(1L, "OperadorEditado", "operador1"));

        mockMvc.perform(put("/operadores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("OperadorEditado"))
                .andExpect(jsonPath("$.usuario").value("operador1"));
    }

    @Test
    public void OperadorController_update_Retorna404CuandoNoExiste() throws Exception {
        UpdateOperadorRequest req = buildUpdateRequest();
        when(operadorService.update(anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new NoSuchElementException("Operador no encontrado."));

        mockMvc.perform(put("/operadores/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void OperadorController_update_Retorna401CuandoCurrentPasswordIncorrecto() throws Exception {
        UpdateOperadorRequest req = buildUpdateRequest();
        when(operadorService.update(anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new SecurityException("Contraseña actual incorrecta."));

        mockMvc.perform(put("/operadores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Contraseña actual incorrecta."));
    }

    @Test
    public void OperadorController_update_Retorna409CuandoUsuarioYaPertenecenAOtro() throws Exception {
        UpdateOperadorRequest req = buildUpdateRequest();
        when(operadorService.update(anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalStateException("El usuario ya pertenece a otro operador."));

        mockMvc.perform(put("/operadores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El usuario ya pertenece a otro operador."));
    }


    @Test
    public void OperadorController_delete_Retorna204CuandoSeElimina() throws Exception {
        doNothing().when(operadorService).delete(1L);

        mockMvc.perform(delete("/operadores/1"))
                .andExpect(status().isNoContent());

        verify(operadorService).delete(1L);
    }

    @Test
    public void OperadorController_delete_Retorna404CuandoNoExiste() throws Exception {
        doThrow(new NoSuchElementException("Operador no encontrado."))
                .when(operadorService).delete(999L);

        mockMvc.perform(delete("/operadores/999"))
                .andExpect(status().isNotFound());
    }
}
