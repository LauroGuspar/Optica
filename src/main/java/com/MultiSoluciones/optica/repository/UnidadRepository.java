package com.MultiSoluciones.optica.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.MultiSoluciones.optica.model.Unidad;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Integer> {

    List<Unidad> findAllByEstadoNot(Integer estado);
    List<Unidad> findAllByEstado(Integer estado);
    Optional<Unidad> findByNombreIgnoreCase(String nombre);

    @Modifying
    @Query("UPDATE Unidad u SET u.estado = :nuevoEstado WHERE u.id = :id")
    void actualizarEstado(Integer id, Integer nuevoEstado);

    boolean existsByNombre(String nombre);
}