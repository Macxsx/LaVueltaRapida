package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RestauranteController {

    //http://localhost:8080
    @GetMapping()
    public String index() {
        return "index";
    }

    //http://localhost:8080/f1-standings
    @GetMapping("/f1-standings")
    public String f1Standings() {
        return "f1-standings";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
    
    