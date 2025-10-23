package com.MultiSoluciones.optica.service;

import java.util.List;
import java.util.Optional;

import com.MultiSoluciones.optica.model.Unidad;

public interface UnidadService {
    List<Unidad> listarTodasLasUnidades();
    Optional<Unidad> obtenerUnidadPorId(Integer id);
    Unidad guardarUnidad(Unidad unidad);
    void eliminarUnidad(Integer id);
    boolean cambiarEstadoUnidad(Integer id);
}