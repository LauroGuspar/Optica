package com.MultiSoluciones.optica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.MultiSoluciones.optica.service.UsuarioService;

@Controller
public class DashboardController {
    private final UsuarioService usuarioService;
    public DashboardController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String mostrarDashboard(Model model) {
        long totalUsuarios = usuarioService.contarUsuarios();

        model.addAttribute("totalUsuarios", totalUsuarios);

        return "index";
    }
}