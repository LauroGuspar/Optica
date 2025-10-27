package com.MultiSoluciones.optica.repository;

import com.MultiSoluciones.optica.model.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoComprobanteRepository extends JpaRepository<TipoComprobante, Integer> {
    List<TipoComprobante> findAllByEstado(int estado);
}
