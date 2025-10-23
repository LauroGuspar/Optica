package com.MultiSoluciones.optica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MultiSoluciones.optica.model.EmpresaSidebarImagen;

@Repository
public interface EmpresaSidebarImagenRepository extends JpaRepository<EmpresaSidebarImagen, Long> {
}