package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.UpdateClienteRequest;
import com.example.demo.entitys.Cliente;
import com.example.demo.exception.CuentaDesactivadaException;
import com.example.demo.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ClienteRestController.class)
@ActiveProfiles("test")
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;


    private Cliente buildCliente(Long id, String username, String email) {
        Cliente c = new Cliente("Pablo", "Pérez", email, username, "123456", "Cra 1 #2-3", "3001234567");
        c.setId(id);
        return c;
    }

    private UpdateClienteRequest buildUpdateRequest() {
        UpdateClienteRequest req = new UpdateClienteRequest();
        req.name = "PabloEditado";
        req.apellido = "Pérez";
        req.email = "pablo@x.com";
        req.username = "pablo123";
        req.password = "nueva";
        req.direccion = "Cra 9";
        req.telefono = "3009999999";
        req.currentPassword = "123456";
        return req;
    }


    @Test
    public void ClienteController_findAll_RetornaColeccionDeClientes() throws Exception {
        when(clienteService.findAll()).thenReturn(List.of(
                buildCliente(1L, "pablo123", "pablo@x.com"),
                buildCliente(2L, "ana456", "ana@x.com")));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("pablo123"))
                .andExpect(jsonPath("$[0].email").value("pablo@x.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("ana456"));
    }

    @Test
    public void ClienteController_findAll_RetornaListaVaciaCuandoNoHayClientes() throws Exception {
        when(clienteService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void ClienteController_findById_RetornaClienteCuandoExiste() throws Exception {
        when(clienteService.findById(1L)).thenReturn(buildCliente(1L, "pablo123", "pablo@x.com"));

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("pablo123"))
                .andExpect(jsonPath("$.email").value("pablo@x.com"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    public void ClienteController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(clienteService.findById(999L))
                .thenThrow(new NoSuchElementException("Cliente no encontrado."));

        mockMvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void ClienteController_add_CreaClienteYRetorna200() throws Exception {
        Cliente nuevo = buildCliente(null, "pablo123", "pablo@x.com");
        when(clienteService.create(any(Cliente.class))).thenReturn(buildCliente(99L, "pablo123", "pablo@x.com"));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.username").value("pablo123"));
    }

    @Test
    public void ClienteController_add_Retorna409CuandoUsuarioOEmailYaExiste() throws Exception {
        Cliente nuevo = buildCliente(null, "pablo123", "pablo@x.com");
        when(clienteService.create(any(Cliente.class)))
                .thenThrow(new IllegalStateException("El username o email ya están en uso."));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El username o email ya están en uso."));
    }


    @Test
    public void ClienteController_update_ActualizaYRetorna200() throws Exception {
        UpdateClienteRequest req = buildUpdateRequest();
        when(clienteService.update(eq(1L), eq("PabloEditado"), eq("Pérez"), eq("pablo@x.com"),
                eq("pablo123"), eq("nueva"), eq("Cra 9"), eq("3009999999"), eq("123456")))
                .thenReturn(buildCliente(1L, "pablo123", "pablo@x.com"));

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("pablo123"));
    }

    @Test
    public void ClienteController_update_Retorna404CuandoNoExiste() throws Exception {
        UpdateClienteRequest req = buildUpdateRequest();
        when(clienteService.update(anyLong(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new NoSuchElementException("Cliente no encontrado."));

        mockMvc.perform(put("/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ClienteController_update_Retorna401CuandoCurrentPasswordIncorrecto() throws Exception {
        UpdateClienteRequest req = buildUpdateRequest();
        when(clienteService.update(anyLong(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new SecurityException("Contraseña actual incorrecta."));

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Contraseña actual incorrecta."));
    }

    @Test
    public void ClienteController_update_Retorna409CuandoUsuarioOEmailYaPertenecenAOtro() throws Exception {
        UpdateClienteRequest req = buildUpdateRequest();
        when(clienteService.update(anyLong(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalStateException("El email ya pertenece a otro cliente."));

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El email ya pertenece a otro cliente."));
    }


    @Test
    public void ClienteController_delete_Retorna204CuandoSeElimina() throws Exception {
        doNothing().when(clienteService).deleteOrDeactivate(1L);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService).deleteOrDeactivate(1L);
    }

    @Test
    public void ClienteController_delete_Retorna200ConMensajeCuandoCuentaSeDesactiva() throws Exception {
        doThrow(new CuentaDesactivadaException("La cuenta fue desactivada por tener pedidos previos."))
                .when(clienteService).deleteOrDeactivate(1L);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("La cuenta fue desactivada por tener pedidos previos."));
    }

    @Test
    public void ClienteController_delete_Retorna404CuandoNoExiste() throws Exception {
        doThrow(new NoSuchElementException("Cliente no encontrado."))
                .when(clienteService).deleteOrDeactivate(999L);

        mockMvc.perform(delete("/clientes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ClienteController_delete_Retorna409CuandoTienePedidosActivos() throws Exception {
        doThrow(new IllegalStateException("El cliente tiene pedidos activos."))
                .when(clienteService).deleteOrDeactivate(1L);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El cliente tiene pedidos activos."));
    }
}
