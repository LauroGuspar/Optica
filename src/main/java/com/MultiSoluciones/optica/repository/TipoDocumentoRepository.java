package com.MultiSoluciones.optica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MultiSoluciones.optica.model.TipoDocumento;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long>{
    List<TipoDocumento> findAllByEstado(Integer estado);
}
