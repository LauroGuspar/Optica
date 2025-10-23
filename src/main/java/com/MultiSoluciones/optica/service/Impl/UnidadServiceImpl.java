package com.MultiSoluciones.optica.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.MultiSoluciones.optica.model.Unidad;
import com.MultiSoluciones.optica.repository.UnidadRepository;
import com.MultiSoluciones.optica.service.UnidadService;

@Service
public class UnidadServiceImpl implements UnidadService {

    private final UnidadRepository unidadRepository;

    public UnidadServiceImpl(UnidadRepository unidadRepository) {
        this.unidadRepository = unidadRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Unidad> listarTodasLasUnidades() {
        return unidadRepository.findAllByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Unidad> obtenerUnidadPorId(Integer id) {
        return unidadRepository.findById(id);
    }

    @Override
    @Transactional
    public Unidad guardarUnidad(Unidad unidad) {
        if (unidad.getId() == null) {
            Optional<Unidad> existenteOpt = unidadRepository.findByNombreIgnoreCase(unidad.getNombre());
            if (existenteOpt.isPresent()) {
                Unidad existente = existenteOpt.get();
                if (existente.getEstado() == 2) {
                    existente.setEstado(1);
                    return unidadRepository.save(existente);
                } else {
                    throw new IllegalArgumentException("Ya existe una unidad con el nombre: " + unidad.getNombre());
                }
            }
        }
        return unidadRepository.save(unidad);
    }

    @Override
    @Transactional
    public void eliminarUnidad(Integer id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada con ID: " + id));
        unidad.setEstado(2);
        unidadRepository.save(unidad);
    }

    @Override
    @Transactional
    public boolean cambiarEstadoUnidad(Integer id) {
        Optional<Unidad> unidadOpt = unidadRepository.findById(id);
        if (unidadOpt.isEmpty()) {
            return false;
        }
        Unidad unidad = unidadOpt.get();
        if (unidad.getEstado() == 1) {
            unidadRepository.actualizarEstado(id, 0);
        } else if (unidad.getEstado() == 0) {
            unidadRepository.actualizarEstado(id, 1);
        }
        return true;
    }
}