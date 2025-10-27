package com.MultiSoluciones.optica.dto;

import java.math.BigDecimal;

public class DetalleVentaRespuestaDTO {
    private String productoNombre;
    private int cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;
    private int stock;
    private Long productoId;

    public DetalleVentaRespuestaDTO(String productoNombre, int cantidad, BigDecimal precio, BigDecimal subtotal,
                                    int stock, Long productoId) {
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
        this.stock = stock;
        this.productoId = productoId;
    }

    // Getters y Setters
    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
}
