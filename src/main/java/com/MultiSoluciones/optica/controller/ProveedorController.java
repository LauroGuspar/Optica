package com.MultiSoluciones.optica.controller;

import com.MultiSoluciones.optica.model.Proveedor;
import com.MultiSoluciones.optica.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaProveedores(Model model) {
        model.addAttribute("formProveedor", new Proveedor());
        return "proveedores";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarProveedoresApi() {
        Map<String, Object> response = new HashMap<>();
        List<Proveedor> proveedores = proveedorService.listarTodosProveedores();
        response.put("success", true);
        response.put("data", proveedores);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerProveedor(@PathVariable Long id) {
        return proveedorService.obtenerProveedorPorId(id)
                .map(proveedor -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", proveedor);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarProveedor(@Valid @RequestBody Proveedor proveedor, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            Map<String, String> errores = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            e -> e.getField(),
                            e -> e.getDefaultMessage()
                    ));
            response.put("success", false);
            response.put("message", "Datos inválidos");
            response.put("errors", errores);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Proveedor proveedorGuardado = proveedorService.guardarProveedor(proveedor);
            response.put("success", true);
            response.put("proveedor", proveedorGuardado);
            response.put("message", proveedor.getId() != null ? "Proveedor actualizado" : "Proveedor creado");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoProveedor(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean exito = proveedorService.cambiarEstadoProveedor(id);
        if (exito) {
            response.put("success", true);
            response.put("message", "Estado del proveedor actualizado");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Proveedor no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarProveedor(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            proveedorService.eliminarProveedor(id);
            response.put("success", true);
            response.put("message", "Proveedor eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar el proveedor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}