package com.MultiSoluciones.optica.dto;

import com.MultiSoluciones.optica.model.FormaPago;
import com.MultiSoluciones.optica.model.MedioPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VentaCreacionDTO {

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "Debe haber al menos un producto")
    @Size(min = 1, message = "Debe haber al menos un producto")
    private List<ProductoVentaDTO> productos;

    @NotNull(message = "Debe seleccionar un tipo de comprobante")
    private Integer idTipoComprobante;
    private FormaPago formaPago;
    private MedioPago medioPago;

    // Campos de crédito
    private BigDecimal pagoInicial;
    private LocalDate fechaVencimiento;
    private Integer cuotas;
    private List<LocalDate> fechasCuotas; // Para programar cada cuota
    private BigDecimal deuda;

    // Getters y Setters

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public List<ProductoVentaDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoVentaDTO> productos) {
        this.productos = productos;
    }

    public Integer getIdTipoComprobante() {
        return idTipoComprobante;
    }

    public void setIdTipoComprobante(Integer idTipoComprobante) {
        this.idTipoComprobante = idTipoComprobante;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public BigDecimal getPagoInicial() {
        return pagoInicial;
    }

    public void setPagoInicial(BigDecimal pagoInicial) {
        this.pagoInicial = pagoInicial;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Integer getCuotas() {
        return cuotas;
    }

    public void setCuotas(Integer cuotas) {
        this.cuotas = cuotas;
    }

    public BigDecimal getDeuda() {
        return deuda;
    }

    public void setDeuda(BigDecimal deuda) {
        this.deuda = deuda;
    }

    public List<LocalDate> getFechasCuotas() {
        return fechasCuotas;
    }

    public void setFechasCuotas(List<LocalDate> fechasCuotas) {
        this.fechasCuotas = fechasCuotas;
    }
}
