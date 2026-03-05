package com.example.demo.controller;

import com.example.demo.entitys.Comida;
import com.example.demo.entitys.Categoria; // Importación explícita para evitar el "missing type"
import com.example.demo.service.CategoriaService;
import com.example.demo.service.ComidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@RequestMapping("/producto")
@Controller
public class MenuController {

    @Autowired
    ComidaService comidaService;

    @Autowired
    CategoriaService categoriaService;

    // http://localhost:5000/producto/menu
    @GetMapping("/menu")
    public String mostrarMenu(Model model) {
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("comidas", comidaService.findAll());
        return "menu";
    }

    // http://localhost:5000/producto/{id}
    @GetMapping("/{id}")
    public String verProducto(@PathVariable Integer id, Model model) {
        model.addAttribute("comida", comidaService.findById(id));
        model.addAttribute("recomendaciones", comidaService.Recomendados(id));
        return "product-detail";
    }

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
        // Ajustado al constructor manual de Comida: (id, name, description, price, image, available, category)
        Comida comida = new Comida(null, "", "", 0.0, "", true, null);
        model.addAttribute("comida", comida);
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("editMode", false);
        return "add-product";
    }

    @PostMapping("/add")
    public String AdicionarComida(@ModelAttribute("comida") Comida comida, @RequestParam("categoryId") Integer categoryId) {
        comida.setCategory(categoriaService.findById(categoryId));
        comidaService.save(comida);
        return "redirect:/producto/menutabla";
    }

    @GetMapping("/update/{id}")
    public String ActualizarComida(@PathVariable("id") Integer id, Model model) {
        Comida comida = comidaService.findById(id);
        model.addAttribute("comida", comida);
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("editMode", true);
        return "add-product";
    }
}