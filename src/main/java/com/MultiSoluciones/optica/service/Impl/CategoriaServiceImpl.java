package com.MultiSoluciones.optica.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.MultiSoluciones.optica.model.Categoria;
import com.MultiSoluciones.optica.repository.CategoriaRepository;
import com.MultiSoluciones.optica.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodasLasCategorias() {
        return categoriaRepository.findAllByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria guardarCategoria(Categoria categoria) {
    if (categoria.getId() == null) {
        Optional<Categoria> existenteOpt = categoriaRepository.findByNombreIgnoreCase(categoria.getNombre());

        if (existenteOpt.isPresent()) {
            Categoria existente = existenteOpt.get();
            if (existente.getEstado() == 2) {
                existente.setEstado(1);
                return categoriaRepository.save(existente);
            } else {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
            }
        }
    }
    return categoriaRepository.save(categoria);
}

    @Override
    @Transactional
    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        categoria.setEstado(2);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public boolean cambiarEstadoCategoria(Long id) {
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(id);
        if (categoriaOpt.isEmpty()) {
            return false;
        }
        Categoria categoria = categoriaOpt.get();
        if (categoria.getEstado() == 1) {
            categoriaRepository.actualizarEstado(id, 0);
        } else if (categoria.getEstado() == 0) {
            categoriaRepository.actualizarEstado(id, 1);
        }
        return true;
    }
}