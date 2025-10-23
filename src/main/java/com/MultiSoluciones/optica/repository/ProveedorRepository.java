package com.MultiSoluciones.optica.repository;

import com.MultiSoluciones.optica.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
    Optional<Proveedor> findByCorreoIgnoreCase(String correo);
    
    Optional<Proveedor> findByTipodocumento_IdAndNdocumento(Long tipodocumentoId, String ndocumento);

    boolean existsByCorreoIgnoreCase(String correo);

    List<Proveedor> findAllByEstadoNot(Integer estado);

    @Modifying
    @Query("UPDATE Proveedor p SET p.estado = :nuevoEstado WHERE p.id = :id")
    void actualizarEstado(Long id, Integer nuevoEstado);
}