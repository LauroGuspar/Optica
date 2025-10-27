package com.MultiSoluciones.optica.controller;

import com.MultiSoluciones.optica.dto.PagarCuotaRequestDTO;
import com.MultiSoluciones.optica.dto.VentaCreacionDTO;
import com.MultiSoluciones.optica.dto.VentaCuotasRespuestaDTO;
import com.MultiSoluciones.optica.dto.VentaDetalleRespuestaDTO;
import com.MultiSoluciones.optica.model.TipoComprobante;
import com.MultiSoluciones.optica.model.Usuario;
import com.MultiSoluciones.optica.model.Venta;
import com.MultiSoluciones.optica.service.IVentaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final IVentaService ventaService;

    public VentaController(IVentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/listar")
    public String listarVentas() {
        return "ventas";
    }

    @GetMapping("/api/listar")
    public ResponseEntity<List<Map<String, Object>>> listarVentasApi(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate fechaFin) {
        List<Venta> ventas = ventaService.listarTodos(fechaInicio, fechaFin);
        List<Map<String, Object>> resultado = ventas.stream().map(venta -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", venta.getId());
            map.put("numeroDocumento",
                    (venta.getTipoComprobante() != null ? venta.getTipoComprobante().getSerie() + "-" : "")
                            + venta.getNumeroDocumento());
            map.put("cliente", venta.getCliente().getNombre());
            map.put("usuario", venta.getUsuario().getNombre());
            map.put("fecha", venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            map.put("total", venta.getTotal());
            map.put("formaPago", venta.getFormaPago() != null ? venta.getFormaPago().toString() : "N/A");
            map.put("deuda", venta.getDeuda() != null ? venta.getDeuda() : 0);
            map.put("estado", venta.getEstado());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/api/tipos-comprobante")
    public ResponseEntity<List<TipoComprobante>> getTiposComprobante() {
        return ResponseEntity.ok(ventaService.listarTiposComprobanteActivos());
    }

    @GetMapping("/api/siguiente-numero/{id}")
    public ResponseEntity<Map<String, String>> getSiguienteNumero(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.getSiguienteNumero(id));
    }

    @PostMapping("/api/guardar")
    public ResponseEntity<Venta> guardarVenta(@Valid @RequestBody VentaCreacionDTO ventaDTO, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Venta nuevaVenta = ventaService.guardarVenta(ventaDTO, usuarioLogueado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }

    @GetMapping("/api/detalle/{id}")
    public ResponseEntity<VentaDetalleRespuestaDTO> getVentaDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.findVentaDetalleById(id));
    }

    @PostMapping("/api/anular/{id}")
    public ResponseEntity<Void> anularVenta(@PathVariable Long id) {
        ventaService.anularVenta(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/cuotas/{id}")
    public ResponseEntity<VentaCuotasRespuestaDTO> getCuotasDeVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.getCuotasByVentaId(id));
    }

    @PostMapping("/api/cuotas/pagar")
    public ResponseEntity<Void> pagarCuota(@Valid @RequestBody PagarCuotaRequestDTO payload) {
        ventaService.pagarCuota(payload.getVentaId(), payload.getNumeroCuota(), payload.getMedioPago());
        return ResponseEntity.ok().build();
    }
}
