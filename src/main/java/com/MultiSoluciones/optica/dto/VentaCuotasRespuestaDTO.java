package com.MultiSoluciones.optica.dto;

import java.math.BigDecimal;
import java.util.List;

public class VentaCuotasRespuestaDTO {
    private String clienteNombre;
    private BigDecimal deudaActual;
    private List<VentaDetalleCuotaDTO> cuotas;

    public VentaCuotasRespuestaDTO(String clienteNombre, BigDecimal deudaActual, List<VentaDetalleCuotaDTO> cuotas) {
        this.clienteNombre = clienteNombre;
        this.deudaActual = deudaActual;
        this.cuotas = cuotas;
    }

    // Getters y Setters
    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public BigDecimal getDeudaActual() {
        return deudaActual;
    }

    public void setDeudaActual(BigDecimal deudaActual) {
        this.deudaActual = deudaActual;
    }

    public List<VentaDetalleCuotaDTO> getCuotas() {
        return cuotas;
    }

    public void setCuotas(List<VentaDetalleCuotaDTO> cuotas) {
        this.cuotas = cuotas;
    }
}