package com.MultiSoluciones.optica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CarritoController {
    @GetMapping("/carrito")
    public String mostrarCarrito(Model model) {

        return "carrito";
    }
}
