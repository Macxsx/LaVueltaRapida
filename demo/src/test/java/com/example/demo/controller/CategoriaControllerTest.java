package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.example.demo.entitys.Categoria;
import com.example.demo.service.CategoriaService;

@WebMvcTest(controllers = CategoriaRestController.class)
@ActiveProfiles("test")
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;


    @Test
    public void CategoriaController_findAll_RetornaColeccionDeCategorias() throws Exception {
        when(categoriaService.findAll())
                .thenReturn(List.of(new Categoria(1L, "Pizzas"), new Categoria(2L, "Bebidas")));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Pizzas"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Bebidas"));
    }

    @Test
    public void CategoriaController_findAll_RetornaListaVaciaCuandoNoHayCategorias() throws Exception {
        when(categoriaService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void CategoriaController_findById_RetornaCategoriaCuandoExiste() throws Exception {
        when(categoriaService.findById(1L)).thenReturn(new Categoria(1L, "Pizzas"));

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizzas"));
    }

    @Test
    public void CategoriaController_findById_Retorna404CuandoNoExiste() throws Exception {
        when(categoriaService.findById(999L))
                .thenThrow(new NoSuchElementException("Categoría no encontrada."));

        mockMvc.perform(get("/categorias/999"))
                .andExpect(status().isNotFound());
    }
}
