package com.MultiSoluciones.optica.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_cliente", nullable=false)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name="cli_nombre",nullable = false)
    private String nombre;

    @Column(name="cli_apellido_paterno")
    private String apellidoPaterno;

    @Column(name="cli_apellido_materno")
    private String apellidoMaterno;

    @Column(name="cli_correo")
    private String correo;

    @Column(name="cli_telefono", unique = true)
    private String telefono;

    @NotBlank(message="La dirección es obligatorio")
    @Column(name="cli_direccion",nullable=false)
    private String direccion;

    @Column(name="cli_estado", nullable = false)
    private Integer estado = 1;

    @NotBlank(message="El Número de Documento es obligatorio")
    @Size(min=8, max=20, message="El Número De Documento debe de tener mínimos 8 Caracteres")
    @Column(name="cli_ndocumento",nullable=false, unique = true)
    private String ndocumento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipodocumento")
    private TipoDocumento tipodocumento;

    public Cliente() {
    }

    public Cliente(String nombre, String apellidoPaterno, String apellidoMaterno, String correo, String telefono, String direccion, String ndocumento, TipoDocumento tipodocumento) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ndocumento = ndocumento;
        this.tipodocumento = tipodocumento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "El nombre es obligatorio") @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres") String getNombre() {
        return nombre;
    }

    public void setNombre(@NotBlank(message = "El nombre es obligatorio") @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres") String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public @NotBlank(message = "La dirección es obligatorio") String getDireccion() {
        return direccion;
    }

    public void setDireccion(@NotBlank(message = "La dirección es obligatorio") String direccion) {
        this.direccion = direccion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public @NotBlank(message = "El Número de Documento es obligatorio") @Size(min = 8, max = 20, message = "El Número De Documento debe de tener mínimos 8 Caracteres") String getNdocumento() {
        return ndocumento;
    }

    public void setNdocumento(@NotBlank(message = "El Número de Documento es obligatorio") @Size(min = 8, max = 20, message = "El Número De Documento debe de tener mínimos 8 Caracteres") String ndocumento) {
        this.ndocumento = ndocumento;
    }

    public TipoDocumento getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(TipoDocumento tipodocumento) {
        this.tipodocumento = tipodocumento;
    }
}
