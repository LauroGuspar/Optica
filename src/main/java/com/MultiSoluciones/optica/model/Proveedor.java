package com.MultiSoluciones.optica.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long id;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(min = 5, max = 100)
    @Column(name = "provee_nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El nombre comercial es obligatorio")
    @Size(min = 5, max = 100)
    @Column(name = "provee_nombre_comercial", nullable = false, length = 100)
    private String nombreComercial;

    @NotBlank(message = "La nacionalidad es obligatoria")
    @Column(name = "provee_nacionalidad", nullable = false, length = 100)
    private String nacionalidad;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(name = "provee_direccion", nullable = false, length = 100)
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 7, max = 9, message = "El teléfono debe tener entre 7 y 9 dígitos")
    @Column(name = "provee_telefono", nullable = false, length = 9)
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Column(name = "provee_correo", nullable = false, length = 150, unique = true)
    private String correo;

    @Email(message = "Formato de correo adicional inválido")
    @Column(name = "provee_correo_adicional", length = 150)
    private String correoAdicional;

    @Column(name = "provee_estado", nullable = false)
    private Integer estado = 1;

    @NotBlank(message = "El RUC es obligatorio")
    @Size(min = 11, max = 11, message = "El RUC debe tener 11 dígitos")
    @Column(name = "provee_ndocumento", nullable = false, length = 20, unique = true)
    private String ndocumento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipodocumento", nullable = false)
    private TipoDocumento tipodocumento;

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

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCorreoAdicional() {
        return correoAdicional;
    }

    public void setCorreoAdicional(String correoAdicional) {
        this.correoAdicional = correoAdicional;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getNdocumento() {
        return ndocumento;
    }

    public void setNdocumento(String ndocumento) {
        this.ndocumento = ndocumento;
    }

    public TipoDocumento getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(TipoDocumento tipodocumento) {
        this.tipodocumento = tipodocumento;
    }
    
    
}