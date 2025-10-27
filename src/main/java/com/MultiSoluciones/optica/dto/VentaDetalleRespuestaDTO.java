package com.MultiSoluciones.optica.dto;

import com.MultiSoluciones.optica.model.FormaPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VentaDetalleRespuestaDTO {
    private Long id;
    private Long idCliente;
    private String cliente;
    private String usuario;
    private String fecha;
    private String numeroDocumento;
    private Integer idTipoComprobante;
    private BigDecimal total;

    // Campos de crédito
    private FormaPago formaPago;
    private BigDecimal pagoInicial;
    private Integer cuotas;
    private List<LocalDate> fechasCuotas;

    private List<DetalleVentaRespuestaDTO> detalle;

    // Constructor privado para ser usado solo por el Builder.
    private VentaDetalleRespuestaDTO(Builder builder) {
        this.id = builder.id;
        this.idCliente = builder.idCliente;
        this.cliente = builder.cliente;
        this.usuario = builder.usuario;
        this.fecha = builder.fecha;
        this.numeroDocumento = builder.numeroDocumento;
        this.idTipoComprobante = builder.idTipoComprobante;
        this.total = builder.total;
        this.formaPago = builder.formaPago;
        this.pagoInicial = builder.pagoInicial;
        this.cuotas = builder.cuotas;
        this.fechasCuotas = builder.fechasCuotas;
        this.detalle = builder.detalle;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Integer getIdTipoComprobante() {
        return idTipoComprobante;
    }

    public void setIdTipoComprobante(Integer idTipoComprobante) {
        this.idTipoComprobante = idTipoComprobante;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public BigDecimal getPagoInicial() {
        return pagoInicial;
    }

    public Integer getCuotas() {
        return cuotas;
    }

    public List<LocalDate> getFechasCuotas() {
        return fechasCuotas;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public void setPagoInicial(BigDecimal pagoInicial) {
        this.pagoInicial = pagoInicial;
    }

    public List<DetalleVentaRespuestaDTO> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<DetalleVentaRespuestaDTO> detalle) {
        this.detalle = detalle;
    }

    // Clase Builder estática anidada
    public static class Builder {
        private Long id;
        private Long idCliente;
        private String cliente;
        private String usuario;
        private String fecha;
        private String numeroDocumento;
        private Integer idTipoComprobante;
        private BigDecimal total;
        private FormaPago formaPago;
        private BigDecimal pagoInicial;
        private Integer cuotas;
        private List<LocalDate> fechasCuotas;
        private List<DetalleVentaRespuestaDTO> detalle;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withIdCliente(Long idCliente) {
            this.idCliente = idCliente;
            return this;
        }

        public Builder withCliente(String cliente) {
            this.cliente = cliente;
            return this;
        }

        public Builder withUsuario(String usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder withFecha(String fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder withNumeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
            return this;
        }

        public Builder withIdTipoComprobante(Integer idTipoComprobante) {
            this.idTipoComprobante = idTipoComprobante;
            return this;
        }

        public Builder withTotal(BigDecimal total) {
            this.total = total;
            return this;
        }

        public Builder withFormaPago(FormaPago formaPago) {
            this.formaPago = formaPago;
            return this;
        }

        public Builder withPagoInicial(BigDecimal pagoInicial) {
            this.pagoInicial = pagoInicial;
            return this;
        }

        public Builder withCuotas(Integer cuotas) {
            this.cuotas = cuotas;
            return this;
        }

        public Builder withFechasCuotas(List<LocalDate> fechasCuotas) {
            this.fechasCuotas = fechasCuotas;
            return this;
        }

        public Builder withDetalle(List<DetalleVentaRespuestaDTO> detalle) {
            this.detalle = detalle;
            return this;
        }

        public VentaDetalleRespuestaDTO build() {
            return new VentaDetalleRespuestaDTO(this);
        }
    }
}
