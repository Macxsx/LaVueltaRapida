package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.config.JwtService;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.Pedido;
import com.example.demo.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PedidoRestController.class,
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@ActiveProfiles("test")
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private JwtService jwtService;


    private Cliente buildCliente(Long id) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setUsername("pablo123");
        c.setEmail("pablo@x.com");
        return c;
    }

    private Pedido buildPedido(Long id, Long clienteId, EstadoPedido estado) {
        Pedido p = new Pedido(id, LocalDateTime.now(), null, estado, buildCliente(clienteId), null);
        return p;
    }


    @Test
    public void PedidoController_findAll_RetornaPaginaConValoresPorDefecto() throws Exception {
        List<Pedido> pedidos = List.of(
                buildPedido(1L, 7L, EstadoPedido.RECIBIDO),
                buildPedido(2L, 8L, EstadoPedido.COCINANDO));
        when(pedidoService.findAll(0, 20))
                .thenReturn(new PageImpl<>(pedidos, PageRequest.of(0, 20), 2));

        mockMvc.perform(get("/pedido"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].estado").value("RECIBIDO"))
                .andExpect(jsonPath("$.content[1].estado").value("COCINANDO"));
    }

    @Test
    public void PedidoController_findAll_RespetaParametrosPageYSize() throws Exception {
        when(pedidoService.findAll(2, 5))
                .thenReturn(new PageImpl<>(List.of(buildPedido(1L, 7L, EstadoPedido.RECIBIDO)),
                        PageRequest.of(2, 5), 11));

        mockMvc.perform(get("/pedido").param("page", "2").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(11))
                .andExpect(jsonPath("$.totalPages").value(3));
    }


    @Test
    public void PedidoController_findActivos_RetornaListaDePedidosActivos() throws Exception {
        when(pedidoService.findActivos()).thenReturn(List.of(
                buildPedido(1L, 7L, EstadoPedido.RECIBIDO),
                buildPedido(2L, 8L, EstadoPedido.ENVIADO)));

        mockMvc.perform(get("/pedido/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("RECIBIDO"))
                .andExpect(jsonPath("$[1].estado").value("ENVIADO"));
    }

    @Test
    public void PedidoController_findActivos_RetornaListaVaciaCuandoNoHayActivos() throws Exception {
        when(pedidoService.findActivos()).thenReturn(List.of());

        mockMvc.perform(get("/pedido/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void PedidoController_findById_RetornaPedidoCuandoExiste() throws Exception {
        when(pedidoService.findById(1L))
                .thenReturn(Optional.of(buildPedido(1L, 7L, EstadoPedido.RECIBIDO)));

        mockMvc.perform(get("/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("RECIBIDO"))
                .andExpect(jsonPath("$.cliente.id").value(7));
    }

    @Test
    public void PedidoController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(pedidoService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pedido/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void PedidoController_findByCliente_RetornaListaDePedidosCuandoExiste() throws Exception {
        when(pedidoService.findByClienteId(7L)).thenReturn(List.of(
                buildPedido(1L, 7L, EstadoPedido.RECIBIDO),
                buildPedido(2L, 7L, EstadoPedido.ENTREGADO)));

        mockMvc.perform(get("/pedido/cliente/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cliente.id").value(7))
                .andExpect(jsonPath("$[1].estado").value("ENTREGADO"));
    }

    @Test
    public void PedidoController_findByCliente_Retorna404CuandoClienteNoExiste() throws Exception {
        when(pedidoService.findByClienteId(999L))
                .thenThrow(new NoSuchElementException("Cliente no encontrado."));

        mockMvc.perform(get("/pedido/cliente/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void PedidoController_desdeCarrito_CreaPedidoYRetorna200() throws Exception {
        when(pedidoService.desdeCarrito(1L, null))
                .thenReturn(buildPedido(99L, 7L, EstadoPedido.RECIBIDO));

        mockMvc.perform(post("/pedido/desde-carrito/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.estado").value("RECIBIDO"))
                .andExpect(jsonPath("$.cliente.id").value(7));
    }

    @Test
    public void PedidoController_desdeCarrito_Retorna404CuandoCarritoNoExiste() throws Exception {
        when(pedidoService.desdeCarrito(999L, null))
                .thenThrow(new NoSuchElementException("Carrito no encontrado."));

        mockMvc.perform(post("/pedido/desde-carrito/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void PedidoController_desdeCarrito_Retorna400CuandoCarritoEstaVacio() throws Exception {
        when(pedidoService.desdeCarrito(1L, null))
                .thenThrow(new IllegalArgumentException("El carrito está vacío."));

        mockMvc.perform(post("/pedido/desde-carrito/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El carrito está vacío."));
    }


    @Test
    public void PedidoController_actualizarEstado_ActualizaYRetorna200() throws Exception {
        when(pedidoService.actualizarEstado(eq(1L), eq("COCINANDO")))
                .thenReturn(buildPedido(1L, 7L, EstadoPedido.COCINANDO));

        mockMvc.perform(patch("/pedido/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "COCINANDO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("COCINANDO"));
    }

    @Test
    public void PedidoController_actualizarEstado_Retorna404CuandoNoExiste() throws Exception {
        when(pedidoService.actualizarEstado(anyLong(), anyString()))
                .thenThrow(new NoSuchElementException("Pedido no encontrado."));

        mockMvc.perform(patch("/pedido/999/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "COCINANDO"))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void PedidoController_actualizarEstado_Retorna400CuandoEstadoEsInvalido() throws Exception {
        when(pedidoService.actualizarEstado(anyLong(), anyString()))
                .thenThrow(new IllegalArgumentException("Estado inválido o transición no permitida."));

        mockMvc.perform(patch("/pedido/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "INEXISTENTE"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Estado inválido o transición no permitida."));
    }

    @Test
    public void PedidoController_actualizarEstado_Retorna409CuandoNoHayDomiciliariosDisponibles() throws Exception {
        when(pedidoService.actualizarEstado(anyLong(), anyString()))
                .thenThrow(new IllegalStateException("No hay domiciliarios disponibles."));

        mockMvc.perform(patch("/pedido/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "ENVIADO"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("No hay domiciliarios disponibles."));
    }


    // ── PATCH /pedido/{id}/pago-presencial ────────────────────────────────────

    @Test
    public void PedidoController_pagoPresencial_Retorna200CuandoPedidoExiste() throws Exception {
        when(pedidoService.confirmarPresencial(eq(1L), eq("EFECTIVO")))
                .thenReturn(buildPedido(1L, 7L, EstadoPedido.RECIBIDO));

        mockMvc.perform(patch("/pedido/1/pago-presencial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("metodoPago", "EFECTIVO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void PedidoController_pagoPresencial_Retorna404CuandoPedidoNoExiste() throws Exception {
        when(pedidoService.confirmarPresencial(anyLong(), anyString()))
                .thenThrow(new NoSuchElementException("Pedido no encontrado."));

        mockMvc.perform(patch("/pedido/999/pago-presencial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("metodoPago", "EFECTIVO"))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void PedidoController_pagoPresencial_Retorna400CuandoMetodoPagoInvalido() throws Exception {
        when(pedidoService.confirmarPresencial(anyLong(), anyString()))
                .thenThrow(new IllegalArgumentException("Método de pago inválido."));

        mockMvc.perform(patch("/pedido/1/pago-presencial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("metodoPago", "INVALIDO"))))
                .andExpect(status().isBadRequest());
    }


    // ── PATCH /pedido/{id}/confirmar-pago ─────────────────────────────────────

    @Test
    public void PedidoController_confirmarPago_Retorna200CuandoPagoConfirmado() throws Exception {
        when(pedidoService.confirmarPago(1L))
                .thenReturn(buildPedido(1L, 7L, EstadoPedido.RECIBIDO));

        mockMvc.perform(patch("/pedido/1/confirmar-pago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void PedidoController_confirmarPago_Retorna404CuandoPedidoNoExiste() throws Exception {
        when(pedidoService.confirmarPago(999L))
                .thenThrow(new NoSuchElementException("Pedido no encontrado."));

        mockMvc.perform(patch("/pedido/999/confirmar-pago"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void PedidoController_confirmarPago_Retorna400CuandoMetodoPagoNoEsContraEntrega() throws Exception {
        when(pedidoService.confirmarPago(anyLong()))
                .thenThrow(new IllegalArgumentException("El pedido no tiene un método de pago contra entrega."));

        mockMvc.perform(patch("/pedido/1/confirmar-pago"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El pedido no tiene un método de pago contra entrega."));
    }

    @Test
    public void PedidoController_confirmarPago_Retorna409CuandoPedidoYaEstaPagado() throws Exception {
        when(pedidoService.confirmarPago(anyLong()))
                .thenThrow(new IllegalStateException("El pedido ya está pagado."));

        mockMvc.perform(patch("/pedido/1/confirmar-pago"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El pedido ya está pagado."));
    }
}
