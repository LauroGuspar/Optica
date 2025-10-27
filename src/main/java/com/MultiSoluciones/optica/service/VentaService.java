package com.MultiSoluciones.optica.service;

import com.MultiSoluciones.optica.dto.*;
import com.MultiSoluciones.optica.model.*;
import com.MultiSoluciones.optica.repository.ClienteRepository;
import com.MultiSoluciones.optica.repository.ProductoRepository;
import com.MultiSoluciones.optica.repository.TipoComprobanteRepository;
import com.MultiSoluciones.optica.repository.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VentaService implements IVentaService {

    private final VentaRepository ventaRepository;
    private final TipoComprobanteRepository tipoComprobanteRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository,
                        TipoComprobanteRepository tipoComprobanteRepository,
                        ClienteRepository clienteRepository,
                        ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.tipoComprobanteRepository = tipoComprobanteRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarTodos(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicioDelDia = fechaInicio.atStartOfDay();
            LocalDateTime finDelDia = fechaFin.atTime(23, 59, 59);
            // Asumo que crearás este método en VentaRepository
            return ventaRepository.findAllWithDetailsByFechaBetween(inicioDelDia, finDelDia);
        } else {
            return ventaRepository.findAllWithDetails();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoComprobante> listarTiposComprobanteActivos() {
        return tipoComprobanteRepository.findAllByEstado(1); // 1 = Activo
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getSiguienteNumero(Integer idTipoComprobante) {
        TipoComprobante tipo = tipoComprobanteRepository.findById(idTipoComprobante)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de comprobante no encontrado con ID: " + idTipoComprobante));

        // Formatear el número a una cadena con ceros a la izquierda (ej. 8 dígitos)
        // Se suma 1 para mostrar el número que se usará, no el último usado.
        String siguienteNumero = String.format("%08d", tipo.getCorrelativoActual() + 1);

        return Map.of("siguienteNumero", siguienteNumero);
    }

    @Override
    @Transactional
    public Venta guardarVenta(VentaCreacionDTO ventaDTO, Usuario usuarioLogueado) {
        // 1. Validar y obtener entidades relacionadas
        Cliente cliente = clienteRepository.findById(ventaDTO.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        // Se obtiene el tipo de comprobante con un bloqueo para evitar condiciones de
        // carrera al asignar el número.
        TipoComprobante tipoComprobante = ventaRepository
                .findTipoComprobanteByIdWithLock(ventaDTO.getIdTipoComprobante());
        if (tipoComprobante == null) {
            throw new EntityNotFoundException("Tipo de comprobante no encontrado");
        }

        // 2. Crear la entidad Venta y asignar datos
        Venta nuevaVenta = new Venta();
        nuevaVenta.setCliente(cliente);
        nuevaVenta.setUsuario(usuarioLogueado);
        nuevaVenta.setFecha(LocalDateTime.now());
        nuevaVenta.setFormaPago(ventaDTO.getFormaPago());
        nuevaVenta.setMedioPago(ventaDTO.getMedioPago());
        nuevaVenta.setTipoComprobante(tipoComprobante);

        // 3. Asignar número de documento e incrementar correlativo
        int numeroActual = tipoComprobante.getCorrelativoActual() + 1;
        nuevaVenta.setNumeroDocumento(String.format("%08d", numeroActual));
        tipoComprobante.setCorrelativoActual(numeroActual);

        // 4. Procesar detalles, calcular total y actualizar stock
        BigDecimal totalVenta = procesarDetalles(nuevaVenta, ventaDTO.getProductos());
        nuevaVenta.setTotal(totalVenta);

        // 5. Manejar lógica de crédito y cuotas
        if (FormaPago.CREDITO.equals(ventaDTO.getFormaPago())) {
            procesarCredito(nuevaVenta, ventaDTO, totalVenta);
        }

        // 6. Guardar la venta (y sus detalles en cascada)
        return ventaRepository.save(nuevaVenta);
    }

    private BigDecimal procesarDetalles(Venta nuevaVenta, List<com.MultiSoluciones.optica.dto.ProductoVentaDTO> productosDTO) {
        BigDecimal totalVenta = BigDecimal.ZERO;

        // Optimización N+1: Obtener todos los productos en una sola consulta.
        List<Long> idsProductos = productosDTO.stream().map(com.MultiSoluciones.optica.dto.ProductoVentaDTO::getId).toList();
        Map<Long, Producto> productosMap = productoRepository.findAllById(idsProductos).stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        for (var itemDTO : productosDTO) {
            Producto producto = productosMap.get(itemDTO.getId());
            if (producto == null) {
                throw new EntityNotFoundException("Producto no encontrado: " + itemDTO.getId());
            }

            if (producto.getStock() < itemDTO.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setVenta(nuevaVenta);
            detalle.setCantidad(itemDTO.getCantidad());
            detalle.setPrecio(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(itemDTO.getCantidad())));

            nuevaVenta.getDetalleVentas().add(detalle);
            totalVenta = totalVenta.add(detalle.getSubtotal());

            producto.setStock(producto.getStock() - itemDTO.getCantidad());
        }
        return totalVenta;
    }

    private void procesarCredito(Venta nuevaVenta, VentaCreacionDTO ventaDTO, BigDecimal totalVenta) {
        BigDecimal pagoInicial = ventaDTO.getPagoInicial() != null ? ventaDTO.getPagoInicial() : BigDecimal.ZERO;
        BigDecimal deudaTotal = totalVenta.subtract(pagoInicial);

        nuevaVenta.setPagoInicial(pagoInicial);
        nuevaVenta.setDeuda(deudaTotal);
        nuevaVenta.setCuotas(ventaDTO.getCuotas());

        boolean tieneCuotasProgramadas = ventaDTO.getCuotas() != null && ventaDTO.getCuotas() > 0
                && ventaDTO.getFechasCuotas() != null && !ventaDTO.getFechasCuotas().isEmpty();

        if (tieneCuotasProgramadas) {
            if (ventaDTO.getCuotas() != ventaDTO.getFechasCuotas().size()) {
                throw new IllegalStateException(
                        "El número de cuotas no coincide con el número de fechas de pago proporcionadas.");
            }

            BigDecimal montoCuota = deudaTotal.divide(BigDecimal.valueOf(ventaDTO.getCuotas()), 2,
                    RoundingMode.HALF_UP);
            for (int i = 0; i < ventaDTO.getCuotas(); i++) {
                PagoCreditoDetalle detalleCuota = new PagoCreditoDetalle();
                detalleCuota.setVenta(nuevaVenta);
                detalleCuota.setNumeroCuota(i + 1);
                detalleCuota.setMonto(montoCuota); // El monto de la cuota
                detalleCuota.setFechaVencimiento(ventaDTO.getFechasCuotas().get(i));
                detalleCuota.setMontoPagado(BigDecimal.ZERO); // Inicializar monto pagado
                detalleCuota.setEstado("PENDIENTE");
                // Los campos de pago se dejan nulos hasta que se pague la cuota
                nuevaVenta.getPagosCredito().add(detalleCuota);
            }
            nuevaVenta.setFechaVencimiento(ventaDTO.getFechasCuotas().get(ventaDTO.getFechasCuotas().size() - 1));
        } else {
            nuevaVenta.setFechaVencimiento(ventaDTO.getFechaVencimiento());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDetalleRespuestaDTO findVentaDetalleById(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));

        List<DetalleVentaRespuestaDTO> detalleDTOs = venta.getDetalleVentas().stream()
                .map(detalle -> new DetalleVentaRespuestaDTO(
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecio(),
                        detalle.getSubtotal(),
                        detalle.getProducto().getStock(), // Añadir stock actual
                        detalle.getProducto().getId())) // Añadir ID del producto
                .collect(Collectors.toList());

        VentaDetalleRespuestaDTO.Builder builder = new VentaDetalleRespuestaDTO.Builder()
                .withId(venta.getId())
                .withIdCliente(venta.getCliente().getId()) // Añadir el ID del cliente
                .withNumeroDocumento(venta.getNumeroDocumento())
                .withCliente(venta.getCliente().getNombre())
                .withUsuario(venta.getUsuario().getNombre())
                .withFecha(venta.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .withTotal(venta.getTotal())
                .withIdTipoComprobante(venta.getTipoComprobante().getId())
                .withFormaPago(venta.getFormaPago())
                .withDetalle(detalleDTOs);

        if (venta.getFormaPago() == FormaPago.CREDITO) {
            builder.withPagoInicial(venta.getPagoInicial())
                    .withCuotas(venta.getCuotas());
            List<LocalDate> fechasCuotas = venta.getPagosCredito().stream().map(PagoCreditoDetalle::getFechaVencimiento)
                    .collect(Collectors.toList());
            builder.withFechasCuotas(fechasCuotas);
        }

        return builder.build();
    }

    @Override
    @Transactional
    public void anularVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));

        // 1. Verificar si la venta ya está anulada
        if (venta.getEstado() == 0) {
            throw new IllegalStateException("La venta ya ha sido anulada previamente.");
        }

        // 2. Devolver el stock de los productos
        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            Producto producto = detalle.getProducto();
            int cantidadVendida = detalle.getCantidad();
            producto.setStock(producto.getStock() + cantidadVendida);
            // No es necesario llamar a productoRepository.save() gracias a la
            // transaccionalidad
        }

        // 3. Cambiar el estado de la venta a "Anulada"
        venta.setEstado(0); // 0 = Anulada
    }

    @Override
    @Transactional(readOnly = true)
    public VentaCuotasRespuestaDTO getCuotasByVentaId(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + ventaId));

        // Si la venta es a crédito pero no tiene un plan de cuotas detallado,
        // devolvemos una respuesta vacía en lugar de lanzar un error.
        if (venta.getFormaPago() != FormaPago.CREDITO || venta.getPagosCredito().isEmpty()) {
            return new VentaCuotasRespuestaDTO(venta.getCliente().getNombre(), venta.getDeuda(), new ArrayList<>());
        }

        List<VentaDetalleCuotaDTO> cuotasDTO = venta.getPagosCredito().stream()
                .map(VentaDetalleCuotaDTO::new) // Usar el nuevo constructor
                .collect(Collectors.toList());

        return new VentaCuotasRespuestaDTO(venta.getCliente().getNombre(), venta.getDeuda(), cuotasDTO);
    }

    @Override
    @Transactional
    public void pagarCuota(Long ventaId, int numeroCuota, MedioPago medioPago) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + ventaId));

        if (venta.getPagosCredito().isEmpty()) {
            throw new IllegalStateException("Esta venta no tiene un plan de cuotas para pagar.");
        }

        PagoCreditoDetalle cuotaAPagar = venta.getPagosCredito().stream()
                .filter(c -> c.getNumeroCuota() == numeroCuota && "PENDIENTE".equals(c.getEstado()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("La cuota no está disponible para pago."));

        // 1. Actualizar estado de la cuota
        cuotaAPagar.setEstado("PAGADO");
        cuotaAPagar.setFechaPago(LocalDateTime.now()); // Establecer la fecha de pago
        cuotaAPagar.setMedioPago(medioPago);

        // 2. Actualizar la deuda total de la venta
        BigDecimal nuevaDeuda = venta.getDeuda().subtract(cuotaAPagar.getMonto());
        venta.setDeuda(nuevaDeuda.max(BigDecimal.ZERO)); // Asegurar que no sea negativa

        // La entidad 'venta' y su 'pagoCreditoDetalle' se guardarán automáticamente al
        // final de la transacción.
    }
}
