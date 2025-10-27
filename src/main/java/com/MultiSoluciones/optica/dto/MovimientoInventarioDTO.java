package com.MultiSoluciones.optica.dto;

import java.math.BigDecimal;
import java.util.List;

public class MovimientoInventarioDTO {
    private List<MovimientoDetalleDTO> movimientos;
    private BigDecimal totalVendido;

    public MovimientoInventarioDTO(List<MovimientoDetalleDTO> movimientos, BigDecimal totalVendido) {
        this.movimientos = movimientos;
        this.totalVendido = totalVendido;
    }

    // Getters y Setters
    public List<MovimientoDetalleDTO> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoDetalleDTO> movimientos) {
        this.movimientos = movimientos;
    }

    public BigDecimal getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(BigDecimal totalVendido) {
        this.totalVendido = totalVendido;
    }
}
