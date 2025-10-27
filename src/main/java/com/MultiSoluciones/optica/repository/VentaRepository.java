package com.MultiSoluciones.optica.repository;

import com.MultiSoluciones.optica.model.Venta;
import com.MultiSoluciones.optica.model.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT v FROM Venta v JOIN FETCH v.cliente JOIN FETCH v.usuario LEFT JOIN FETCH v.tipoComprobante ORDER BY v.fecha DESC")
    List<Venta> findAllWithDetails();

    @Query("SELECT v FROM Venta v JOIN FETCH v.cliente JOIN FETCH v.usuario LEFT JOIN FETCH v.tipoComprobante WHERE v.fecha BETWEEN :inicio AND :fin ORDER BY v.fecha DESC")
    List<Venta> findAllWithDetailsByFechaBetween(@Param("inicio") LocalDateTime inicio,
                                                 @Param("fin") LocalDateTime fin);

    @Query("SELECT tc FROM TipoComprobante tc WHERE tc.id = :id")
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    TipoComprobante findTipoComprobanteByIdWithLock(@Param("id") Integer id);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin AND v.estado = :estado")
    Optional<BigDecimal> sumTotalByFechaBetweenAndEstado(@Param("inicio") LocalDateTime inicio,
                                                         @Param("fin") LocalDateTime fin, @Param("estado") int estado);

    @Query("SELECT SUM(v.deuda) FROM Venta v WHERE v.estado = :estado AND v.deuda > 0")
    Optional<BigDecimal> sumDeudaByEstado(@Param("estado") int estado);

    @Query("SELECT v FROM Venta v JOIN FETCH v.cliente JOIN FETCH v.usuario WHERE v.estado = 1 AND v.deuda > 0 ORDER BY v.fecha ASC")
    List<Venta> findCuentasPorCobrar();

}
