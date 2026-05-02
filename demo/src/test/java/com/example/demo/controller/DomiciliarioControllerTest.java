package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.entitys.Domiciliario;
import com.example.demo.service.DomiciliarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = DomiciliarioRestController.class)
@ActiveProfiles("test")
public class DomiciliarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DomiciliarioService domiciliarioService;


    private Domiciliario buildDomiciliario(Long id, String nombre, String cedula, String celular, boolean disponible) {
        return new Domiciliario(id, nombre, cedula, celular, disponible);
    }


    @Test
    public void DomiciliarioController_findAll_RetornaListaDeDomiciliarios() throws Exception {
        when(domiciliarioService.findAll()).thenReturn(List.of(
                buildDomiciliario(1L, "Carlos", "1001", "3001111111", true),
                buildDomiciliario(2L, "Ana", "1002", "3002222222", false)));

        mockMvc.perform(get("/domiciliarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Carlos"))
                .andExpect(jsonPath("$[0].cedula").value("1001"))
                .andExpect(jsonPath("$[0].celular").value("3001111111"))
                .andExpect(jsonPath("$[0].disponible").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].disponible").value(false));
    }

    @Test
    public void DomiciliarioController_findAll_RetornaListaVaciaCuandoNoHayDomiciliarios() throws Exception {
        when(domiciliarioService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/domiciliarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void DomiciliarioController_findById_RetornaDomiciliarioCuandoExiste() throws Exception {
        when(domiciliarioService.findById(1L))
                .thenReturn(Optional.of(buildDomiciliario(1L, "Carlos", "1001", "3001111111", true)));

        mockMvc.perform(get("/domiciliarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.cedula").value("1001"))
                .andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    public void DomiciliarioController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(domiciliarioService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/domiciliarios/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void DomiciliarioController_add_CreaDomiciliarioYRetorna200() throws Exception {
        Domiciliario nuevo = buildDomiciliario(null, "Carlos", "1001", "3001111111", true);
        when(domiciliarioService.create(any(Domiciliario.class)))
                .thenReturn(buildDomiciliario(99L, "Carlos", "1001", "3001111111", true));

        mockMvc.perform(post("/domiciliarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.cedula").value("1001"));
    }

    @Test
    public void DomiciliarioController_add_Retorna409CuandoCedulaOCelularYaExiste() throws Exception {
        Domiciliario nuevo = buildDomiciliario(null, "Carlos", "1001", "3001111111", true);
        when(domiciliarioService.create(any(Domiciliario.class)))
                .thenThrow(new IllegalStateException("La cédula o el celular ya están en uso."));

        mockMvc.perform(post("/domiciliarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La cédula o el celular ya están en uso."));
    }


    @Test
    public void DomiciliarioController_update_ActualizaYRetorna200() throws Exception {
        Domiciliario data = buildDomiciliario(null, "Carlos Editado", "1001", "3009999999", false);
        when(domiciliarioService.update(eq(1L), any(Domiciliario.class)))
                .thenReturn(buildDomiciliario(1L, "Carlos Editado", "1001", "3009999999", false));

        mockMvc.perform(put("/domiciliarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos Editado"))
                .andExpect(jsonPath("$.celular").value("3009999999"))
                .andExpect(jsonPath("$.disponible").value(false));
    }

    @Test
    public void DomiciliarioController_update_Retorna404CuandoNoExiste() throws Exception {
        Domiciliario data = buildDomiciliario(null, "Carlos", "1001", "3001111111", true);
        when(domiciliarioService.update(anyLong(), any(Domiciliario.class)))
                .thenThrow(new NoSuchElementException("Domiciliario no encontrado."));

        mockMvc.perform(put("/domiciliarios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void DomiciliarioController_update_Retorna409CuandoCedulaOCelularYaPertenecenAOtro() throws Exception {
        Domiciliario data = buildDomiciliario(null, "Carlos", "1001", "3001111111", true);
        when(domiciliarioService.update(anyLong(), any(Domiciliario.class)))
                .thenThrow(new IllegalStateException("La cédula ya pertenece a otro domiciliario."));

        mockMvc.perform(put("/domiciliarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La cédula ya pertenece a otro domiciliario."));
    }


    @Test
    public void DomiciliarioController_delete_Retorna204CuandoSeElimina() throws Exception {
        doNothing().when(domiciliarioService).delete(1L);

        mockMvc.perform(delete("/domiciliarios/1"))
                .andExpect(status().isNoContent());

        verify(domiciliarioService).delete(1L);
    }

    @Test
    public void DomiciliarioController_delete_Retorna404CuandoNoExiste() throws Exception {
        doThrow(new NoSuchElementException("Domiciliario no encontrado."))
                .when(domiciliarioService).delete(999L);

        mockMvc.perform(delete("/domiciliarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void DomiciliarioController_delete_Retorna409CuandoTienePedidoEnCurso() throws Exception {
        doThrow(new IllegalStateException("El domiciliario tiene un pedido en curso."))
                .when(domiciliarioService).delete(1L);

        mockMvc.perform(delete("/domiciliarios/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El domiciliario tiene un pedido en curso."));
    }
}
