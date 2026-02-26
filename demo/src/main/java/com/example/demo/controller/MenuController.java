package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.ComidaService;


@RequestMapping("/producto")
@Controller
public class MenuController {

    @Autowired
    ComidaService comidaService;



    //http://localhost:8080/menu
    @GetMapping("/menu")
    public String mostrarMenu(Model model) {
    model.addAttribute("comidas", comidaService.findAll());
        return "menu";
    }

    @GetMapping("/{id}")
    public String verProducto(@PathVariable int id, Model model) {
    model.addAttribute("comida", comidaService.findById(id));
    model.addAttribute("recomendaciones", comidaService.findTop2ByIdGreaterThanOrderByIdAsc(id));
    return "product-detail";
    }   

        @GetMapping("/menutabla")
    public String mostrarMenuTabla(Model model) {
    model.addAttribute("comidas", comidaService.findAll());
        return "menu-list";
    }
}
    
    