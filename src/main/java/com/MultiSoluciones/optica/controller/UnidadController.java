package com.MultiSoluciones.optica.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.MultiSoluciones.optica.model.Unidad;
import com.MultiSoluciones.optica.service.UnidadService;

@Controller
@RequestMapping("/productos/unidades")
public class UnidadController {

    private final UnidadService unidadService;

    public UnidadController(UnidadService unidadService) {
        this.unidadService = unidadService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaUnidades() {
        return "unidades";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarUnidadesApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", unidadService.listarTodasLasUnidades());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerUnidad(@PathVariable Integer id) {
        return unidadService.obtenerUnidadPorId(id)
                .map(unidad -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", unidad);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarUnidad(@RequestBody Unidad unidad) {
        Map<String, Object> response = new HashMap<>();
        try {
            Unidad unidadGuardada = unidadService.guardarUnidad(unidad);
            response.put("success", true);
            response.put("unidad", unidadGuardada);
            response.put("message", unidad.getId() != null ? "Unidad actualizada" : "Unidad creada");
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
    public ResponseEntity<?> cambiarEstadoUnidad(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        boolean exito = unidadService.cambiarEstadoUnidad(id);
        if (exito) {
            response.put("success", true);
            response.put("message", "Estado de la unidad actualizado");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Unidad no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarUnidad(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            unidadService.eliminarUnidad(id);
            response.put("success", true);
            response.put("message", "Unidad eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la unidad: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}