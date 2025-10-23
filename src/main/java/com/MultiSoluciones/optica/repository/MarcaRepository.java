package com.MultiSoluciones.optica.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.MultiSoluciones.optica.model.Marca;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findAllByEstadoNot(Integer estado);
    List<Marca> findAllByEstado(Integer estado);
    Optional<Marca> findByNombreIgnoreCase(String nombre);

    @Modifying
    @Query("UPDATE Marca m SET m.estado = :nuevoEstado WHERE m.id = :id")
    void actualizarEstado(Long id, Integer nuevoEstado);

    boolean existsByNombre(String nombre);
}