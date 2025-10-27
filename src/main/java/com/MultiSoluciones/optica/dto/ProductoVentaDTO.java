package com.MultiSoluciones.optica.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ProductoVentaDTO {
    @NotNull
    private Long id;

    @NotNull
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
