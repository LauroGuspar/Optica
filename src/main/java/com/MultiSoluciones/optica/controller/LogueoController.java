package com.MultiSoluciones.optica.controller;

import java.util.Comparator;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.MultiSoluciones.optica.model.Opcion;
import com.MultiSoluciones.optica.model.Usuario;
import com.MultiSoluciones.optica.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogueoController {

    private final UsuarioService usuarioService;

    public LogueoController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/logueo")
    public String mostrarFormularioLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/catalogo";
        }
        return "logueo";
    }

    @PostMapping("/logueo")
    public String procesarLogin(@RequestParam String usuario, @RequestParam String clave, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.findByUsuario(usuario);

        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/logueo";
        }

        Usuario usuarioEncontrado = usuarioOpt.get();

        if (usuarioEncontrado.getEstado() != 1) {
            redirectAttributes.addFlashAttribute("error", "Este usuario se encuentra inactivo.");
            return "redirect:/logueo";
        }

        if (usuarioService.verificarContrasena(clave, usuarioEncontrado.getClave())) {
            session.setAttribute("usuarioLogueado", usuarioEncontrado);

            var opcionesMenu = usuarioEncontrado.getPerfil().getOpciones().stream()
                    .sorted(Comparator.comparing(Opcion::getId))
                    .toList();
            session.setAttribute("menuOpciones", opcionesMenu);
            session.setAttribute("perfil", usuarioEncontrado.getPerfil().getId());

            return "redirect:/catalogo";
        } else {
            redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta.");
            return "redirect:/logueo";
        }
    }

    @GetMapping("/cerrar-sesion")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logout", "Has cerrado sesión exitosamente.");
        return "redirect:/logueo";
    }
}
