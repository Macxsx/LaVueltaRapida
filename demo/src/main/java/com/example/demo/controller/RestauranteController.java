package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.service.ComidaService;



@Controller
public class RestauranteController {

    @Autowired
    ComidaService comidaService;

    //http://localhost:8080
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("comidas", comidaService.findAll());
        return "index";
    }

    //http://localhost:8080/f1-standings
    @GetMapping("/f1-standings")
    public String f1Standings() {
        return "f1-standings";
    }



    

}
    
    