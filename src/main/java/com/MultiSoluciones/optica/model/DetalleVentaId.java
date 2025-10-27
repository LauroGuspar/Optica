package com.MultiSoluciones.optica.model;

import java.io.Serializable;
import java.util.Objects;

public class DetalleVentaId implements Serializable {
    private Long venta;
    private Long producto;

    // Constructores, equals y hashCode
    public DetalleVentaId() {
    }

    public DetalleVentaId(Long venta, Long producto) {
        this.venta = venta;
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DetalleVentaId that = (DetalleVentaId) o;
        return Objects.equals(venta, that.venta) && Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(venta, producto);
    }
}
