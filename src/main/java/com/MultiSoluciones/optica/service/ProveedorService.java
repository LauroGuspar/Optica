package com.MultiSoluciones.optica.service;

import com.MultiSoluciones.optica.model.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorService {
    List<Proveedor> listarTodosProveedores();
    Optional<Proveedor> obtenerProveedorPorId(Long id);
    Proveedor guardarProveedor(Proveedor proveedor);
    void eliminarProveedor(Long id);
    boolean cambiarEstadoProveedor(Long id);
}