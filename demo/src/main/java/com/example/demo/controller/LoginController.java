package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.entitys.Cliente;
import com.example.demo.service.ClienteService;


@Controller
public class LoginController {

    @Autowired
    ClienteService clienteService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        Cliente cliente = new Cliente(null, null, null, null, null, null, null, null);
        model.addAttribute("cliente", cliente);
        model.addAttribute("editMode", false);
        return "register";
    }
    

    @PostMapping("/login")
    public String loginPost(@RequestParam String username, @RequestParam String password) {
        if (username.equals("admin") && password.equals("admin")) {
            return "redirect:/producto/menutabla";
        }

        if (clienteService.validateCredentials(username, password)) {
            return "redirect:/index";
        } else {
            return "redirect:/login?error";
        }
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute Cliente cliente) {
        clienteService.save(cliente);
        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String Perfil(Model model, @RequestParam int id) {
        model.addAttribute("cliente", clienteService.findById(id)); 
        return "perfil";
    }
    

    @GetMapping("/perfil/delete")
    public String deletePerfil(@RequestParam int id) {
        clienteService.deleteById(id);
        return "redirect:/index";
    }

    @GetMapping("/perfil/update")
    public String updatePerfil(Model model, @RequestParam int id) {
        model.addAttribute("cliente", clienteService.findById(id));
        model.addAttribute("editMode", true);
        return "register";
    }
    
    

    

    

    


}
