package com.MultiSoluciones.optica.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @Column(name = "id_empresa")
    private Long id;

    @Column(name = "empresa_nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "empresa_direccion", nullable = false, length = 100)
    private String direccion;

    @Column(name = "empresa_ruc", nullable = false, length = 11)
    private String ruc;

    @Column(name = "empresa_logo", nullable = false, length = 255)
    private String logo;

    @OneToMany(
        mappedBy = "empresa", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true
    )
    private List<EmpresaSidebarImagen> sidebarImagenes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<EmpresaSidebarImagen> getSidebarImagenes() {
        return sidebarImagenes;
    }

    public void setSidebarImagenes(List<EmpresaSidebarImagen> sidebarImagenes) {
        this.sidebarImagenes = sidebarImagenes;
    }
}