package com.MultiSoluciones.optica.service;

import com.MultiSoluciones.optica.dto.VentaCreacionDTO;
import com.MultiSoluciones.optica.dto.VentaCuotasRespuestaDTO;
import com.MultiSoluciones.optica.dto.VentaDetalleRespuestaDTO;
import com.MultiSoluciones.optica.model.MedioPago;
import com.MultiSoluciones.optica.model.TipoComprobante;
import com.MultiSoluciones.optica.model.Usuario;
import com.MultiSoluciones.optica.model.Venta;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IVentaService {
    List<Venta> listarTodos(LocalDate fechaInicio, LocalDate fechaFin);

    List<TipoComprobante> listarTiposComprobanteActivos();

    Map<String, String> getSiguienteNumero(Integer idTipoComprobante);

    Venta guardarVenta(VentaCreacionDTO ventaDTO, Usuario usuarioLogueado);

    VentaDetalleRespuestaDTO findVentaDetalleById(Long id);

    void anularVenta(Long id);

    VentaCuotasRespuestaDTO getCuotasByVentaId(Long ventaId);

    void pagarCuota(Long ventaId, int numeroCuota, MedioPago medioPago);
}
