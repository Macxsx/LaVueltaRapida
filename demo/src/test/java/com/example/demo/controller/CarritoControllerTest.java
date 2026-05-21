package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.example.demo.dto.AddProductoRequest;
import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.service.CarritoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CarritoRestController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
public class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarritoService carritoService;

    @MockitoBean
    private JwtService jwtService;


    private Carrito buildCarrito(Long id, Long clienteId) {
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        Carrito carrito = new Carrito(cliente);
        carrito.setId(id);
        carrito.setActivo(true);
        return carrito;
    }

    private AddProductoRequest buildAddRequest(Long comidaId, Integer cantidad, List<Long> adicionalesIds) {
        AddProductoRequest req = new AddProductoRequest();
        req.comidaId = comidaId;
        req.cantidad = cantidad;
        req.adicionalesIds = adicionalesIds;
        return req;
    }


    @Test
    public void CarritoController_findById_RetornaCarritoCuandoExiste() throws Exception {
        when(carritoService.findById(1L)).thenReturn(Optional.of(buildCarrito(1L, 7L)));

        mockMvc.perform(get("/carrito/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cliente.id").value(7))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    public void CarritoController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(carritoService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/carrito/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void CarritoController_findByCliente_RetornaCarritoCuandoExiste() throws Exception {
        when(carritoService.findByClienteId(7L)).thenReturn(Optional.of(buildCarrito(1L, 7L)));

        mockMvc.perform(get("/carrito/cliente/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cliente.id").value(7));
    }

    @Test
    public void CarritoController_findByCliente_Retorna404CuandoNoExiste() throws Exception {
        when(carritoService.findByClienteId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/carrito/cliente/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void CarritoController_addProducto_AgregaYRetorna200() throws Exception {
        AddProductoRequest req = buildAddRequest(10L, 2, List.of(5L, 6L));
        when(carritoService.addProducto(eq(1L), eq(10L), eq(2), any()))
                .thenReturn(buildCarrito(1L, 7L));

        mockMvc.perform(post("/carrito/1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cliente.id").value(7));
    }

    @Test
    public void CarritoController_addProducto_Retorna404CuandoCarritoOComidaNoExiste() throws Exception {
        AddProductoRequest req = buildAddRequest(10L, 2, List.of());
        when(carritoService.addProducto(anyLong(), anyLong(), anyInt(), any()))
                .thenThrow(new NoSuchElementException("Carrito no encontrado."));

        mockMvc.perform(post("/carrito/999/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void CarritoController_addProducto_Retorna400CuandoArgumentoInvalido() throws Exception {
        AddProductoRequest req = buildAddRequest(10L, 0, List.of());
        when(carritoService.addProducto(anyLong(), anyLong(), anyInt(), any()))
                .thenThrow(new IllegalArgumentException("La cantidad debe ser mayor a cero."));

        mockMvc.perform(post("/carrito/1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La cantidad debe ser mayor a cero."));
    }


    @Test
    public void CarritoController_removeProducto_RemueveYRetorna200() throws Exception {
        when(carritoService.removeProducto(1L, 5L)).thenReturn(buildCarrito(1L, 7L));

        mockMvc.perform(delete("/carrito/1/productos/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carritoService).removeProducto(1L, 5L);
    }

    @Test
    public void CarritoController_removeProducto_Retorna404CuandoNoExiste() throws Exception {
        when(carritoService.removeProducto(anyLong(), anyLong()))
                .thenThrow(new NoSuchElementException("Línea no encontrada."));

        mockMvc.perform(delete("/carrito/1/productos/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void CarritoController_aumentar_AumentaYRetorna200() throws Exception {
        when(carritoService.aumentarCantidad(1L, 5L)).thenReturn(buildCarrito(1L, 7L));

        mockMvc.perform(patch("/carrito/1/productos/5/aumentar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carritoService).aumentarCantidad(1L, 5L);
    }

    @Test
    public void CarritoController_aumentar_Retorna404CuandoNoExiste() throws Exception {
        when(carritoService.aumentarCantidad(anyLong(), anyLong()))
                .thenThrow(new NoSuchElementException("Línea no encontrada."));

        mockMvc.perform(patch("/carrito/1/productos/999/aumentar"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void CarritoController_disminuir_DisminuyeYRetorna200() throws Exception {
        when(carritoService.disminuirCantidad(1L, 5L)).thenReturn(buildCarrito(1L, 7L));

        mockMvc.perform(patch("/carrito/1/productos/5/disminuir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carritoService).disminuirCantidad(1L, 5L);
    }

    @Test
    public void CarritoController_disminuir_Retorna404CuandoNoExiste() throws Exception {
        when(carritoService.disminuirCantidad(anyLong(), anyLong()))
                .thenThrow(new NoSuchElementException("Línea no encontrada."));

        mockMvc.perform(patch("/carrito/1/productos/999/disminuir"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void CarritoController_vaciar_VaciaYRetorna200() throws Exception {
        when(carritoService.vaciar(1L)).thenReturn(buildCarrito(1L, 7L));

        mockMvc.perform(delete("/carrito/1/vaciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carritoService).vaciar(1L);
    }

    @Test
    public void CarritoController_vaciar_Retorna404CuandoNoExiste() throws Exception {
        when(carritoService.vaciar(999L))
                .thenThrow(new NoSuchElementException("Carrito no encontrado."));

        mockMvc.perform(delete("/carrito/999/vaciar"))
                .andExpect(status().isNotFound());
    }
}
