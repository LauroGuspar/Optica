package com.MultiSoluciones.optica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_comprobante")
public class TipoComprobante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String serie;

    @Column(name = "correlativo_actual")
    private Integer correlativoActual;

    private Integer estado;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getCorrelativoActual() {
        return correlativoActual;
    }

    public void setCorrelativoActual(Integer correlativoActual) {
        this.correlativoActual = correlativoActual;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}
