package com.MultiSoluciones.optica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CatalogoController {
    @GetMapping("/catalogo")
    public String mostrarCatalogo(Model model) {

        return "catalogo";
    }
}
