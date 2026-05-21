package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
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
import com.example.demo.dto.ComidaRequest;
import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Comida;
import com.example.demo.service.ComidaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ComidaRestController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
public class ComidaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ComidaService comidaService;

    @MockitoBean
    private JwtService jwtService;


    private Categoria buildCategoria(Long id, String nombre) {
        Categoria cat = new Categoria();
        cat.setId(id);
        cat.setName(nombre);
        return cat;
    }

    private Comida buildComida(Long id, String name, double price, Long categoriaId) {
        Comida c = new Comida(name, "Descripción de " + name, price, "img.png", true,
                buildCategoria(categoriaId, "Pizzas"));
        c.setId(id);
        return c;
    }

    private ComidaRequest buildRequest(String name, double price, Long categoryId) {
        ComidaRequest req = new ComidaRequest();
        req.name = name;
        req.description = "Descripción de " + name;
        req.price = price;
        req.image = "img.png";
        req.available = true;
        req.categoryId = categoryId;
        return req;
    }


    @Test
    public void ComidaController_findAll_RetornaColeccionDeComidas() throws Exception {
        when(comidaService.findAll()).thenReturn(List.of(
                buildComida(1L, "Margarita", 25000.0, 10L),
                buildComida(2L, "Pepperoni", 28000.0, 10L)));

        mockMvc.perform(get("/comidas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Margarita"))
                .andExpect(jsonPath("$[0].price").value(25000.0))
                .andExpect(jsonPath("$[0].category.id").value(10))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Pepperoni"));
    }

    @Test
    public void ComidaController_findAll_RetornaListaVaciaCuandoNoHayComidas() throws Exception {
        when(comidaService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/comidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void ComidaController_findById_RetornaComidaCuandoExiste() throws Exception {
        when(comidaService.findById(1L)).thenReturn(buildComida(1L, "Margarita", 25000.0, 10L));

        mockMvc.perform(get("/comidas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Margarita"))
                .andExpect(jsonPath("$.price").value(25000.0))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.category.id").value(10));
    }

    @Test
    public void ComidaController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(comidaService.findById(999L))
                .thenThrow(new NoSuchElementException("Comida no encontrada."));

        mockMvc.perform(get("/comidas/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void ComidaController_add_CreaComidaYRetorna200() throws Exception {
        ComidaRequest req = buildRequest("Margarita", 25000.0, 10L);
        when(comidaService.create(eq("Margarita"), eq("Descripción de Margarita"), eq(25000.0),
                eq("img.png"), eq(true), eq(10L)))
                .thenReturn(buildComida(99L, "Margarita", 25000.0, 10L));

        mockMvc.perform(post("/comidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.name").value("Margarita"))
                .andExpect(jsonPath("$.category.id").value(10));
    }

    @Test
    public void ComidaController_add_Retorna400CuandoArgumentoInvalido() throws Exception {
        ComidaRequest req = buildRequest("", -5.0, 10L);
        when(comidaService.create(anyString(), anyString(), anyDouble(), anyString(), anyBoolean(), anyLong()))
                .thenThrow(new IllegalArgumentException("El precio debe ser mayor a cero."));

        mockMvc.perform(post("/comidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El precio debe ser mayor a cero."));
    }


    @Test
    public void ComidaController_update_ActualizaYRetorna200() throws Exception {
        ComidaRequest req = buildRequest("Margarita XL", 32000.0, 10L);
        when(comidaService.update(eq(1L), eq("Margarita XL"), eq("Descripción de Margarita XL"),
                eq(32000.0), eq("img.png"), eq(true), eq(10L)))
                .thenReturn(buildComida(1L, "Margarita XL", 32000.0, 10L));

        mockMvc.perform(put("/comidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Margarita XL"))
                .andExpect(jsonPath("$.price").value(32000.0));
    }

    @Test
    public void ComidaController_update_Retorna404CuandoNoExiste() throws Exception {
        ComidaRequest req = buildRequest("Margarita", 25000.0, 10L);
        when(comidaService.update(anyLong(), anyString(), anyString(), anyDouble(),
                anyString(), anyBoolean(), anyLong()))
                .thenThrow(new NoSuchElementException("Comida no encontrada."));

        mockMvc.perform(put("/comidas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ComidaController_update_Retorna400CuandoArgumentoInvalido() throws Exception {
        ComidaRequest req = buildRequest("Margarita", -5.0, 10L);
        when(comidaService.update(anyLong(), anyString(), anyString(), anyDouble(),
                anyString(), anyBoolean(), anyLong()))
                .thenThrow(new IllegalArgumentException("El precio debe ser mayor a cero."));

        mockMvc.perform(put("/comidas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El precio debe ser mayor a cero."));
    }


    @Test
    public void ComidaController_delete_Retorna204CuandoSeElimina() throws Exception {
        doNothing().when(comidaService).delete(1L);

        mockMvc.perform(delete("/comidas/1"))
                .andExpect(status().isNoContent());

        verify(comidaService).delete(1L);
    }

    @Test
    public void ComidaController_delete_Retorna404CuandoNoExiste() throws Exception {
        doThrow(new NoSuchElementException("Comida no encontrada."))
                .when(comidaService).delete(999L);

        mockMvc.perform(delete("/comidas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ComidaController_delete_Retorna409CuandoEstaEnPedido() throws Exception {
        doThrow(new IllegalStateException("La comida ya está incluida en un pedido."))
                .when(comidaService).delete(1L);

        mockMvc.perform(delete("/comidas/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La comida ya está incluida en un pedido."));
    }
}
