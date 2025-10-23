package com.MultiSoluciones.optica.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.MultiSoluciones.optica.model.Producto;

public interface ProductoService {
List<Producto> listarTodosLosProductos();
    Optional<Producto> obtenerProductoPorId(Long id);
    Producto guardarProducto(Producto producto, List<MultipartFile> imagenFiles) throws IOException;    
    void eliminarProducto(Long id) throws IOException;
    boolean cambiarEstadoProducto(Long id);
}