package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.service.ComidaService;



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
}
    
    