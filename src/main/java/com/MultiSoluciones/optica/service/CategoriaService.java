package com.MultiSoluciones.optica.service;

import java.util.List;
import java.util.Optional;

import com.MultiSoluciones.optica.model.Categoria;

public interface CategoriaService {
    List<Categoria> listarTodasLasCategorias();

    Optional<Categoria> obtenerCategoriaPorId(Long id);
    Categoria guardarCategoria(Categoria categoria);
    
    void eliminarCategoria(Long id);
    boolean cambiarEstadoCategoria(Long id);
}