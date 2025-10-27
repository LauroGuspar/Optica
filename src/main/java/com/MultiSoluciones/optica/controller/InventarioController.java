package com.MultiSoluciones.optica.controller;

import com.MultiSoluciones.optica.dto.MovimientoInventarioDTO;
import com.MultiSoluciones.optica.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/listar")
    public String listarInventario() {
        return "inventario";
    }

    @GetMapping("/api/movimientos/{productoId}")
    @ResponseBody
    public ResponseEntity<MovimientoInventarioDTO> getMovimientosPorProducto(@PathVariable Long productoId) {
        MovimientoInventarioDTO movimientos = inventarioService.getMovimientosPorProducto(productoId);
        return ResponseEntity.ok(movimientos);
    }
}
