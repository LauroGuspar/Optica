package com.MultiSoluciones.optica.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.MultiSoluciones.optica.model.Opcion;
import com.MultiSoluciones.optica.model.Perfil;
import com.MultiSoluciones.optica.repository.OpcionRepository;
import com.MultiSoluciones.optica.repository.PerfilRepository;
import com.MultiSoluciones.optica.service.PerfilService;

@Service
public class PerfilServiceImpl implements PerfilService {

    private final PerfilRepository perfilRepository;
    private final OpcionRepository opcionRepository;

    public PerfilServiceImpl(PerfilRepository perfilRepository, OpcionRepository opcionRepository) {
        this.perfilRepository = perfilRepository;
        this.opcionRepository = opcionRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Perfil> listarTodosLosPerfiles() {
        return perfilRepository.findAllByEstadoNot(2);
    }

    @Override
    @Transactional
    public Perfil guardarPerfil(Perfil perfil) {
if (perfil.getId() == null) {
        Optional<Perfil> existenteOpt = perfilRepository.findByNombreIgnoreCase(perfil.getNombre());
        if (existenteOpt.isPresent()) {
            Perfil existente = existenteOpt.get();
            if (existente.getEstado() == 2) {
                existente.setEstado(1);
                existente.setDescripcion(perfil.getDescripcion());
                return perfilRepository.save(existente);
            } else {
                throw new IllegalArgumentException("Ya existe un perfil con el nombre: " + perfil.getNombre());
            }
        }
    }
    return perfilRepository.save(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Perfil> obtenerPerfilPorId(Long id) {
        return perfilRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<Perfil> cambiarEstadoPerfil(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return obtenerPerfilPorId(id).map(perfil -> {
            if (perfil.getEstado() == 1) {
                perfil.setEstado(0);
            } else if (perfil.getEstado() == 0) {
                perfil.setEstado(1);
            }
            return perfilRepository.save(perfil);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Opcion> listarTodasLasOpciones() {
        return opcionRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarPerfil(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de perfil invÃ¡lido");
        }

        Perfil perfil = obtenerPerfilPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));

        perfil.setEstado(2);
        perfilRepository.save(perfil);
    }
}