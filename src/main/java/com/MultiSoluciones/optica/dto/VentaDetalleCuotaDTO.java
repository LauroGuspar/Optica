package com.MultiSoluciones.optica.dto;

import com.MultiSoluciones.optica.model.MedioPago;
import com.MultiSoluciones.optica.model.PagoCreditoDetalle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VentaDetalleCuotaDTO {
    private int numero;
    private LocalDate fecha;
    private BigDecimal monto;
    private String estado; // ej. PENDIENTE, PAGADO

    // Nuevos campos para el historial
    private LocalDateTime fechaPago;
    private MedioPago medioPago;

    // Constructor para Jackson
    public VentaDetalleCuotaDTO() {
    }

    public VentaDetalleCuotaDTO(int numero, LocalDate fecha, BigDecimal monto, String estado) {
        this.numero = numero;
        this.fecha = fecha;
        this.monto = monto;
        this.estado = estado;
    }

    // Nuevo constructor para mapear desde la entidad
    public VentaDetalleCuotaDTO(PagoCreditoDetalle entity) {
        this.numero = entity.getNumeroCuota();
        this.fecha = entity.getFechaVencimiento();
        this.monto = entity.getMonto();
        this.estado = entity.getEstado();
        this.fechaPago = entity.getFechaPago();
        this.medioPago = entity.getMedioPago();
    }

    // Getters y Setters
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }
}
