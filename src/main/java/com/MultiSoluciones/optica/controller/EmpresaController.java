package com.MultiSoluciones.optica.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.MultiSoluciones.optica.service.EmpresaService;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

   private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }
    
    @GetMapping("/configuracion")
    public String mostrarPaginaConfiguracion() {
        return "empresa";
    }

    @GetMapping("/api/datos")
    @ResponseBody
    public ResponseEntity<?> obtenerDatosEmpresa() {
        return empresaService.getEmpresa().map(empresa -> {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", empresa);
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/api/actualizar", consumes = {"multipart/form-data"})
    @ResponseBody
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> actualizarEmpresa(
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestParam(value = "sidebarFiles", required = false) List<MultipartFile> sidebarFiles
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            empresaService.actualizarEmpresa(logoFile, sidebarFiles); 
            
            response.put("success", true);
            response.put("message", "Empresa actualizada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar la empresa: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/api/sidebar/eliminar/{idImagen}")
    @ResponseBody
    public ResponseEntity<?> eliminarImagenSidebar(@PathVariable Long idImagen) {
        Map<String, Object> response = new HashMap<>();
        try {
            empresaService.eliminarImagenSidebar(idImagen);
            response.put("success", true);
            response.put("message", "Imagen eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la imagen: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}