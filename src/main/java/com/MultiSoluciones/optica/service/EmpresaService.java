package com.MultiSoluciones.optica.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.MultiSoluciones.optica.model.Empresa;
import org.springframework.web.multipart.MultipartFile;

public interface EmpresaService {
    Optional<Empresa> getEmpresa();
    Empresa actualizarEmpresa(MultipartFile logoFile, List<MultipartFile> sidebarFiles) throws IOException;
    void eliminarImagenSidebar(Long idImagen) throws IOException;
}