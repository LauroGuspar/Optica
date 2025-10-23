package com.MultiSoluciones.optica.service.Impl;

import com.MultiSoluciones.optica.model.Proveedor;
import com.MultiSoluciones.optica.model.TipoDocumento;
import com.MultiSoluciones.optica.repository.ProveedorRepository;
import com.MultiSoluciones.optica.repository.TipoDocumentoRepository;
import com.MultiSoluciones.optica.service.ProveedorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private static final Long RUC_TIPO_ID = 2L;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository, TipoDocumentoRepository tipoDocumentoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> listarTodosProveedores() {
        return proveedorRepository.findAllByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Proveedor> obtenerProveedorPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    @Override
    @Transactional
    public Proveedor guardarProveedor(Proveedor proveedor) {
        TipoDocumento rucTipo = tipoDocumentoRepository.findById(RUC_TIPO_ID)
                .orElseThrow(() -> new RuntimeException("Tipo de Documento 'RUC' (ID 2) no encontrado en la BD."));
        proveedor.setTipodocumento(rucTipo);

        if (proveedor.getId() == null) { 
            Optional<Proveedor> existentePorDoc = proveedorRepository.findByTipodocumento_IdAndNdocumento(
                    RUC_TIPO_ID, proveedor.getNdocumento());

            if (existentePorDoc.isPresent()) {
                Proveedor existente = existentePorDoc.get();
                if (existente.getEstado() == 2) {
                    if (proveedorRepository.existsByCorreoIgnoreCase(proveedor.getCorreo()) && 
                        !existente.getCorreo().equalsIgnoreCase(proveedor.getCorreo())) {
                         throw new IllegalArgumentException("El correo electrónico ya está en uso por otra cuenta activa.");
                    }
                    
                    existente.setEstado(1);
                    existente.setNombre(proveedor.getNombre());
                    existente.setNombreComercial(proveedor.getNombreComercial());
                    existente.setNacionalidad(proveedor.getNacionalidad());
                    existente.setDireccion(proveedor.getDireccion());
                    existente.setTelefono(proveedor.getTelefono());
                    existente.setCorreo(proveedor.getCorreo());
                    existente.setCorreoAdicional(proveedor.getCorreoAdicional());
                    return proveedorRepository.save(existente);
                } else {
                    throw new IllegalArgumentException("Ya existe un proveedor registrado con este RUC.");
                }
            }

            if (proveedorRepository.existsByCorreoIgnoreCase(proveedor.getCorreo())) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado.");
            }
            
            proveedor.setEstado(1);
            return proveedorRepository.save(proveedor);

        } else {
            Proveedor existente = proveedorRepository.findById(proveedor.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado para actualizar"));
            Optional<Proveedor> porCorreo = proveedorRepository.findByCorreoIgnoreCase(proveedor.getCorreo());
            if (porCorreo.isPresent() && !porCorreo.get().getId().equals(existente.getId())) {
                throw new IllegalArgumentException("El correo electrónico ya está en uso por otro proveedor.");
            }
            proveedor.setTipodocumento(rucTipo);
            return proveedorRepository.save(proveedor);
        }
    }

    @Override
    @Transactional
    public void eliminarProveedor(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + id));
        proveedor.setEstado(2);
        proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional
    public boolean cambiarEstadoProveedor(Long id) {
        Optional<Proveedor> proveedorOpt = proveedorRepository.findById(id);
        if (proveedorOpt.isEmpty()) {
            return false;
        }
        Proveedor proveedor = proveedorOpt.get();
        if (proveedor.getEstado() == 1) {
            proveedorRepository.actualizarEstado(id, 0);
        } else if (proveedor.getEstado() == 0) {
            proveedorRepository.actualizarEstado(id, 1);
        }
        return true;
    }
}