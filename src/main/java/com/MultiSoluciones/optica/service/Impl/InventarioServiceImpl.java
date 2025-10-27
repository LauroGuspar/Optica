package com.MultiSoluciones.optica.service.Impl;

import com.MultiSoluciones.optica.dto.MovimientoDetalleDTO;
import com.MultiSoluciones.optica.dto.MovimientoInventarioDTO;
import com.MultiSoluciones.optica.model.DetalleVenta;
import com.MultiSoluciones.optica.repository.DetalleVentaRepository;
import com.MultiSoluciones.optica.service.InventarioService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final DetalleVentaRepository detalleVentaRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public InventarioServiceImpl(DetalleVentaRepository detalleVentaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Override
    public MovimientoInventarioDTO getMovimientosPorProducto(Long productoId) {
        // Buscamos solo detalles de ventas que no estén anuladas (estado = 1)
        List<DetalleVenta> detalles = detalleVentaRepository.findByProducto_IdAndVenta_Estado(productoId, 1);

        List<MovimientoDetalleDTO> movimientos = detalles.stream()
                .map(detalle -> new MovimientoDetalleDTO(
                        detalle.getVenta().getFecha().format(FORMATTER),
                        detalle.getVenta().getTipoComprobante().getSerie() + "-"
                                + detalle.getVenta().getNumeroDocumento(),
                        detalle.getPrecio(),
                        detalle.getCantidad(),
                        detalle.getSubtotal()))
                .collect(Collectors.toList());

        BigDecimal totalVendido = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MovimientoInventarioDTO(movimientos, totalVendido);
    }
}