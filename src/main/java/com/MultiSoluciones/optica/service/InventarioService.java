package com.MultiSoluciones.optica.service;

import com.MultiSoluciones.optica.dto.MovimientoInventarioDTO;

public interface InventarioService {

    MovimientoInventarioDTO getMovimientosPorProducto(Long productoId);

}