package com.MultiSoluciones.optica.service.Impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID; 

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.MultiSoluciones.optica.model.Empresa;
import com.MultiSoluciones.optica.model.EmpresaSidebarImagen;
import com.MultiSoluciones.optica.repository.EmpresaRepository;
import com.MultiSoluciones.optica.repository.EmpresaSidebarImagenRepository;
import com.MultiSoluciones.optica.service.EmpresaService;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private static final Long EMPRESA_ID = 1L;

    private final EmpresaRepository empresaRepository;
    private final EmpresaSidebarImagenRepository sidebarImagenRepository;

    @Value("${file.upload-dir-empresa-logo}")
    private String uploadDirLogo;

    @Value("${file.upload-dir-empresa-sidebar}")
    private String uploadDirSidebar;

    public EmpresaServiceImpl(EmpresaRepository empresaRepository, 
                            EmpresaSidebarImagenRepository sidebarImagenRepository) {
        this.empresaRepository = empresaRepository;
        this.sidebarImagenRepository = sidebarImagenRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> getEmpresa() {
        return empresaRepository.findById(EMPRESA_ID);
    }

    @Override
    @Transactional
    @SuppressWarnings("CallToPrintStackTrace")
    public Empresa actualizarEmpresa(MultipartFile logoFile, List<MultipartFile> sidebarFiles) throws IOException {
        
        Empresa empresa = empresaRepository.findById(EMPRESA_ID)
                .orElseThrow(() -> new IllegalStateException("Configuración de empresa no encontrada (ID: " + EMPRESA_ID + ")"));
        if (logoFile != null && !logoFile.isEmpty()) {
            if (empresa.getLogo() != null && !empresa.getLogo().isEmpty()) {
                try {
                    eliminarImagen(empresa.getLogo(), uploadDirLogo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String nuevoNombreLogo = guardarImagen(logoFile, uploadDirLogo);
            empresa.setLogo(nuevoNombreLogo);
        }
        
        if (sidebarFiles != null && !sidebarFiles.isEmpty()) {
            for (MultipartFile sidebarFile : sidebarFiles) {
                if (!sidebarFile.isEmpty()) {
                    String nuevoNombreSidebar = guardarImagen(sidebarFile, uploadDirSidebar);
                    EmpresaSidebarImagen nuevaImagen = new EmpresaSidebarImagen();
                    nuevaImagen.setRuta(nuevoNombreSidebar);
                    nuevaImagen.setEmpresa(empresa);
                    empresa.getSidebarImagenes().add(nuevaImagen);
                }
            }
        }

        return empresaRepository.save(empresa);
    }

    @Override
    @Transactional
    public void eliminarImagenSidebar(Long idImagen) throws IOException {
        EmpresaSidebarImagen imagen = sidebarImagenRepository.findById(idImagen)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada con ID: " + idImagen));

        eliminarImagen(imagen.getRuta(), uploadDirSidebar);

        sidebarImagenRepository.delete(imagen);
    }
    private String guardarImagen(MultipartFile imagenFile, String uploadDir) throws IOException {
        Path directorio = Paths.get(uploadDir);
        if (Files.notExists(directorio)) {
            Files.createDirectories(directorio);
        }
        
        String nombreUnico = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();
        Path rutaCompleta = directorio.resolve(nombreUnico);

        Files.write(rutaCompleta, imagenFile.getBytes());
        return nombreUnico;
    }

    private void eliminarImagen(String nombreImagen, String uploadDir) throws IOException {
        if (nombreImagen == null || nombreImagen.isEmpty()) return;
        
        Path rutaImagen = Paths.get(uploadDir).resolve(nombreImagen);
        Files.deleteIfExists(rutaImagen);
    }
}