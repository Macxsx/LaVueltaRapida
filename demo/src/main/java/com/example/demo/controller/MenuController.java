package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.entitys.Comida;
import com.example.demo.service.ComidaService;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.service.CategoriaService;



@RequestMapping("/producto")
@Controller
public class MenuController {

    @Autowired
    ComidaService comidaService;

    @Autowired
    CategoriaService categoriaService;

    // http://localhost:8080/producto
    @GetMapping("/")
    public String home() {
        return "index";
    }

    //http://localhost:8080/producto/menu
    @GetMapping("/menu")
    public String mostrarMenu(Model model) {
    model.addAttribute("categorias", categoriaService.findAll());
    model.addAttribute("comidas", comidaService.findAll());
        return "menu";
    }

    //http://localhost:8080/producto/{id}
    @GetMapping("/{id}")
    public String verProducto(@PathVariable Integer id, Model model) {
    model.addAttribute("comida", comidaService.findById(id));
    model.addAttribute("recomendaciones", comidaService.Recomendados(id));
    return "product-detail";
    }   

    //http://localhost:8080/producto/menutabla
    @GetMapping("/menutabla")
    public String mostrarMenuTabla(Model model) {
    model.addAttribute("categorias", categoriaService.findAll());
    model.addAttribute("comidas", comidaService.findAll());
        return "menu-list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        comidaService.deleteById(id);
        return "redirect:/producto/menutabla";
    }

    @GetMapping("/add")
    public String MostrarFormularioCrear(Model model) {
        Comida comida = new Comida(null, "", "", 0.0f, "COP", "", true, null);
        model.addAttribute("comida", comida);
        model.addAttribute("categorias", categoriaService.findAll());
        return "add-product";
    }

    @PostMapping("/add")
    public String AdicionarComida(@ModelAttribute("comida") Comida comida,
                                  @org.springframework.web.bind.annotation.RequestParam("categoryId") Integer categoryId) {
        comida.setCategory(categoriaService.findById(categoryId));
        comidaService.save(comida);
        return "redirect:/producto/menu";
    }
    
    @GetMapping("/update/{id}")
    public String ActualizarComida(@PathVariable("id") Integer id, Model model) {
        Comida comida = comidaService.findById(id);
        model.addAttribute("comida", comida);
        return "redirect:/producto/menu";

    }
    
    
    
}
    
    