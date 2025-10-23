package com.MultiSoluciones.optica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MultiSoluciones.optica.model.Opcion;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Long> {
}