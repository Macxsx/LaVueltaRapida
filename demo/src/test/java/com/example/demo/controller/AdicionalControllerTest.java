package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.example.demo.dto.AdicionalRequest;
import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;
import com.example.demo.service.AdicionalService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AdicionalRestController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
public class AdicionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdicionalService adicionalService;

    @MockitoBean
    private JwtService jwtService;


    private AdicionalRequest buildRequest(String name, double price, boolean available, Long[] cats) {
        AdicionalRequest req = new AdicionalRequest();
        req.name = name;
        req.price = price;
        req.available = available;
        req.categoriaIds = cats;
        return req;
    }


    @Test
    public void AdicionalController_findAll_RetornaColeccionDeAdicionales() throws Exception {
        when(adicionalService.findAll()).thenReturn(List.of(
                new Adicional(1L, "Queso", 3000.0, true),
                new Adicional(2L, "Tocineta", 4500.0, true)));

        mockMvc.perform(get("/adicionales"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Queso"))
                .andExpect(jsonPath("$[0].price").value(3000.0))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Tocineta"));
    }

    @Test
    public void AdicionalController_findAll_RetornaListaVaciaCuandoNoHayAdicionales() throws Exception {
        when(adicionalService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/adicionales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void AdicionalController_findByCategoriaId_RetornaAdicionalesDeLaCategoria() throws Exception {
        when(adicionalService.findByCategoriaId(5L)).thenReturn(List.of(
                new Adicional(1L, "Queso", 3000.0, true)));

        mockMvc.perform(get("/adicionales/categoria/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Queso"));
    }


    @Test
    public void AdicionalController_findById_RetornaAdicionalCuandoExiste() throws Exception {
        when(adicionalService.findById(1L)).thenReturn(new Adicional(1L, "Queso", 3000.0, true));

        mockMvc.perform(get("/adicionales/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Queso"))
                .andExpect(jsonPath("$.price").value(3000.0))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void AdicionalController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(adicionalService.findById(999L))
                .thenThrow(new NoSuchElementException("Adicional no encontrado."));

        mockMvc.perform(get("/adicionales/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void AdicionalController_findCategorias_RetornaCategoriasDelAdicional() throws Exception {
        when(adicionalService.findCategorias(1L)).thenReturn(List.of(
                new Categoria(10L, "Pizzas"),
                new Categoria(20L, "Hamburguesas")));

        mockMvc.perform(get("/adicionales/1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Pizzas"))
                .andExpect(jsonPath("$[1].id").value(20))
                .andExpect(jsonPath("$[1].name").value("Hamburguesas"));
    }


    @Test
    public void AdicionalController_add_CreaAdicionalYRetorna200() throws Exception {
        AdicionalRequest req = buildRequest("Queso", 3000.0, true, new Long[]{1L, 2L});
        when(adicionalService.create(eq("Queso"), eq(3000.0), eq(true), any(Long[].class)))
                .thenReturn(new Adicional(99L, "Queso", 3000.0, true));

        mockMvc.perform(post("/adicionales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.name").value("Queso"))
                .andExpect(jsonPath("$.price").value(3000.0))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void AdicionalController_add_Retorna400CuandoArgumentoInvalido() throws Exception {
        AdicionalRequest req = buildRequest("", -1.0, true, new Long[]{1L});
        when(adicionalService.create(anyString(), anyDouble(), anyBoolean(), any()))
                .thenThrow(new IllegalArgumentException("El nombre no puede estar vacío."));

        mockMvc.perform(post("/adicionales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nombre no puede estar vacío."));
    }


    @Test
    public void AdicionalController_update_ActualizaYRetorna200() throws Exception {
        AdicionalRequest req = buildRequest("Queso doble", 3500.0, true, new Long[]{1L});
        when(adicionalService.update(eq(1L), eq("Queso doble"), eq(3500.0), eq(true), any(Long[].class)))
                .thenReturn(new Adicional(1L, "Queso doble", 3500.0, true));

        mockMvc.perform(put("/adicionales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Queso doble"))
                .andExpect(jsonPath("$.price").value(3500.0));
    }

    @Test
    public void AdicionalController_update_Retorna404CuandoNoExiste() throws Exception {
        AdicionalRequest req = buildRequest("Queso", 3000.0, true, new Long[]{1L});
        when(adicionalService.update(anyLong(), anyString(), anyDouble(), anyBoolean(), any()))
                .thenThrow(new NoSuchElementException("Adicional no encontrado."));

        mockMvc.perform(put("/adicionales/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void AdicionalController_update_Retorna400CuandoArgumentoInvalido() throws Exception {
        AdicionalRequest req = buildRequest("", -1.0, true, new Long[]{1L});
        when(adicionalService.update(anyLong(), anyString(), anyDouble(), anyBoolean(), any()))
                .thenThrow(new IllegalArgumentException("Precio inválido."));

        mockMvc.perform(put("/adicionales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Precio inválido."));
    }


    @Test
    public void AdicionalController_delete_Retorna204CuandoSeElimina() throws Exception {
        doNothing().when(adicionalService).delete(1L);

        mockMvc.perform(delete("/adicionales/1"))
                .andExpect(status().isNoContent());

        verify(adicionalService).delete(1L);
    }

    @Test
    public void AdicionalController_delete_Retorna404CuandoNoExiste() throws Exception {
        doThrow(new NoSuchElementException("Adicional no encontrado."))
                .when(adicionalService).delete(999L);

        mockMvc.perform(delete("/adicionales/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void AdicionalController_delete_Retorna409CuandoEstaEnPedido() throws Exception {
        doThrow(new IllegalStateException("El adicional está incluido en un pedido."))
                .when(adicionalService).delete(1L);

        mockMvc.perform(delete("/adicionales/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El adicional está incluido en un pedido."));
    }
}
