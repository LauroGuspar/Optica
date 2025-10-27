package com.MultiSoluciones.optica.dto;

import java.math.BigDecimal;

public class MovimientoDetalleDTO {
    private String fechaVenta;
    private String numeroDocumento;
    private BigDecimal precioVenta;
    private int cantidad;
    private BigDecimal subtotal;

    public MovimientoDetalleDTO(String fechaVenta, String numeroDocumento, BigDecimal precioVenta, int cantidad,
                                BigDecimal subtotal) {
        this.fechaVenta = fechaVenta;
        this.numeroDocumento = numeroDocumento;
        this.precioVenta = precioVenta;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
