package com.MultiSoluciones.optica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MultiSoluciones.optica.model.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}