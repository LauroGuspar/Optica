package com.MultiSoluciones.optica.repository;

import com.MultiSoluciones.optica.model.DetalleVenta;
import com.MultiSoluciones.optica.model.DetalleVentaId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, DetalleVentaId> {
    List<DetalleVenta> findByProducto_IdAndVenta_Estado(Long productoId, int estado);

//    @Query("SELECT new com.example.acceso.dto.TopProductoDTO(d.producto.nombre, SUM(d.cantidad), SUM(d.subtotal)) " +
//            "FROM DetalleVenta d WHERE d.venta.estado = 1 GROUP BY d.producto.id, d.producto.nombre ORDER BY SUM(d.cantidad) DESC")
//    List<TopProductoDTO> findTopSellingProducts(Pageable pageable);
}
