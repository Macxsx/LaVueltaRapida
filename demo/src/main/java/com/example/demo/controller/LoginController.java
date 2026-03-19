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

import jakarta.servlet.http.HttpSession;


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
        Cliente cliente = new Cliente();
        model.addAttribute("cliente", cliente);
        model.addAttribute("editMode", false);
        return "register";
    }
    

    @PostMapping("/login")
    public String loginPost(@RequestParam String username, @RequestParam String password, HttpSession session) {
        if (username.equals("admin") && password.equals("admin")) {
            session.setAttribute("loggedUser", "Admin");
            session.setAttribute("loggedUserId", 0);
            return "redirect:/producto/menutabla";
        }

        if (clienteService.validateCredentials(username, password)) {
            Cliente cliente = clienteService.findByUsername(username);
            session.setAttribute("loggedUser", cliente.getName());
            session.setAttribute("loggedUserId", cliente.getId());
            return "redirect:/";
        } else {
            return "redirect:/login?error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute Cliente cliente, HttpSession session) {
        if (clienteService.isUsernameTaken(cliente.getUsername())) {
            return "redirect:/register?usernameTaken";
        }
        clienteService.save(cliente);
        session.setAttribute("loggedUser", cliente.getName());
        session.setAttribute("loggedUserId", cliente.getId());
        return "redirect:/";
    }

@GetMapping("/perfil")
public String perfil(
        @RequestParam(required = false, defaultValue = "false") boolean edit,
        Model model,
        HttpSession session) {

    Long id = (Long) session.getAttribute("loggedUserId");

    if (id == null) {
        return "redirect:/login";
    }

    Cliente cliente = clienteService.findById(id);

    model.addAttribute("cliente", cliente);
    model.addAttribute("editMode", edit);

    return "perfil";
}
    


    @PostMapping("/perfil/delete")
    public String deletePerfil(HttpSession session) {
        Long id = (Long) session.getAttribute("loggedUserId");
        if (id == null) return "redirect:/login";
        try {
            clienteService.deleteById(id);
            session.invalidate();
            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/perfil?error=delete";
        }
    }

    @PostMapping("/perfil/update")
    public String updatePerfil(@ModelAttribute Cliente cliente, HttpSession session) {

        Long id = (Long) session.getAttribute("loggedUserId");
        Cliente stored = clienteService.findById(id);

        if (!stored.getPassword().equals(cliente.getPassword())) {
            return "redirect:/perfil?edit=true&wrongPassword";
        }

        if (clienteService.isUsernameTakenByOther(cliente.getUsername(), id)) {
            return "redirect:/perfil?edit=true&usernameTaken";
        }

        stored.setName(cliente.getName());
        stored.setApellido(cliente.getApellido());
        stored.setEmail(cliente.getEmail());
        stored.setUsername(cliente.getUsername());
        stored.setDireccion(cliente.getDireccion());
        stored.setTelefono(cliente.getTelefono());

        clienteService.save(stored);
        session.setAttribute("loggedUser", stored.getName());

        return "redirect:/perfil?success";
    }
    

    

    

    


}
