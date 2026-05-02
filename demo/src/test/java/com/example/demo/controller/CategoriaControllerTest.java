package com.example.demo.controller;


import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.demo.entitys.Categoria;
import com.example.demo.service.CategoriaService;

@WebMvcTest(controllers = CategoriaRestController.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private CategoriaService categoriaService;
    

    @Test
    public void CategoriaController_FindAll_ColeccionCategorias() throws Exception{

        when(categoriaService.findAll()).thenReturn(List.of(new Categoria(1L, "Categoria 1"), new Categoria(2L, "Categoria 2")));

        mockMvc.perform(get("/categorias")).andExpect(status().isOk());
    }
}
