package com.MultiSoluciones.optica.service;

import java.util.List;
import java.util.Optional;

import com.MultiSoluciones.optica.model.Opcion;
import com.MultiSoluciones.optica.model.Perfil;

public interface PerfilService {
    List<Perfil> listarTodosLosPerfiles();

    Perfil guardarPerfil(Perfil perfil);

    Optional<Perfil> obtenerPerfilPorId(Long id);

    Optional<Perfil> cambiarEstadoPerfil(Long id);

    List<Opcion> listarTodasLasOpciones();

    void eliminarPerfil(Long id);
}