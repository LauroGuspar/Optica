package com.MultiSoluciones.optica.dto;

import com.MultiSoluciones.optica.model.MedioPago;
import jakarta.validation.constraints.NotNull;

public class PagarCuotaRequestDTO {
    @NotNull
    private Long ventaId;

    @NotNull
    private Integer numeroCuota;

    @NotNull
    private MedioPago medioPago;

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }
}
