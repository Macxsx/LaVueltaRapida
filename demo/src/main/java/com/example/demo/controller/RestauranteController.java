package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entitys.Comida;
import com.example.demo.service.ComidaService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5000")
public class RestauranteController {

    @Autowired
    private ComidaService comidaService;

    // 🏠 HOME DATA (all comidas)
    @GetMapping("/")
    public List<Comida> getHomeData() {
        return new ArrayList<>(comidaService.findAll());
    }

    // 🏎️ Placeholder endpoint (if you still need it)
    @GetMapping("/f1-standings")
    public String f1Standings() {
        return "F1 standings endpoint working";
    }
}