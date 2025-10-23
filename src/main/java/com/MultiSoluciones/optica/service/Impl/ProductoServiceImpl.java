package com.MultiSoluciones.optica.service.Impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.MultiSoluciones.optica.model.Categoria;
import com.MultiSoluciones.optica.model.Marca;
import com.MultiSoluciones.optica.model.Producto;
import com.MultiSoluciones.optica.model.Unidad;
import com.MultiSoluciones.optica.repository.CategoriaRepository;
import com.MultiSoluciones.optica.repository.MarcaRepository;
import com.MultiSoluciones.optica.repository.ProductoRepository;
import com.MultiSoluciones.optica.repository.UnidadRepository;
import com.MultiSoluciones.optica.service.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final UnidadRepository unidadRepository; 
    @Value("${file.upload-dir}")
    private String uploadDir;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, MarcaRepository marcaRepository, UnidadRepository unidadRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
        this.unidadRepository = unidadRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodosLosProductos() {
        return productoRepository.findAllByEstadoNot(2);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    @Transactional
    @SuppressWarnings("CallToPrintStackTrace")
    public Producto guardarProducto(Producto producto, List<MultipartFile> imagenFiles) throws IOException {
        Categoria categoria = categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Marca marca = marcaRepository.findById(producto.getMarca().getId())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        Unidad unidad = unidadRepository.findById(producto.getUnidad().getId())
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

        producto.setCategoria(categoria);
        producto.setMarca(marca);
        producto.setUnidad(unidad);

        String oldImagesStr = null;
        if (producto.getId() == null) {
            producto.setFechaCreacion(LocalDate.now());
        } else {
            oldImagesStr = productoRepository.findById(producto.getId())
                                            .map(Producto::getImagen)
                                            .orElse(null);
        }
        if (imagenFiles != null && !imagenFiles.isEmpty() && !imagenFiles.get(0).isEmpty()) {
            if (oldImagesStr != null && !oldImagesStr.isEmpty()) {
                try {
                    eliminarMultiplesImagenes(oldImagesStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<String> nombresGuardados = new ArrayList<>();
            for (MultipartFile imagenFile : imagenFiles) {
                if (imagenFile != null && !imagenFile.isEmpty()) {
                    String nombreImagen = guardarImagen(imagenFile, categoria.getNombre());
                    nombresGuardados.add(nombreImagen);
                }
            }
            
            if (!nombresGuardados.isEmpty()) {
                producto.setImagen(String.join(",", nombresGuardados));
            } else {
                producto.setImagen(null);
            }
            
        } else {
            String newImagesStr = producto.getImagen();
            if (oldImagesStr != null && !oldImagesStr.isEmpty()) {
                List<String> oldImages = Arrays.asList(oldImagesStr.split(","));
                List<String> newImages = (newImagesStr != null && !newImagesStr.isEmpty()) 
                                        ? Arrays.asList(newImagesStr.split(",")) 
                                        : new ArrayList<>();
                
                for (String oldImg : oldImages) {
                    oldImg = oldImg.trim();
                    if (!newImages.contains(oldImg)) {
                        try {
                            eliminarImagen(oldImg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) throws IOException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getImagen() != null) {
            eliminarMultiplesImagenes(producto.getImagen());
        }
        producto.setEstado(2);
        producto.setImagen(null);
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public boolean cambiarEstadoProducto(Long id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isEmpty() || productoOpt.get().getEstado() == 2) {
            return false;
        }
        int nuevoEstado = productoOpt.get().getEstado() == 1 ? 0 : 1;
        productoRepository.actualizarEstado(id, nuevoEstado);
        return true;
    }

    private String guardarImagen(MultipartFile imagenFile, String categoriaNombre) throws IOException {
        String nombreCarpeta = categoriaNombre.replaceAll("[^a-zA-Z0-9.-]", "_");
        Path directorioCategoria = Paths.get(uploadDir, nombreCarpeta);
        if (Files.notExists(directorioCategoria)) {
            Files.createDirectories(directorioCategoria);
        }
        String nombreUnico = UUID.randomUUID().toString() + "_" + imagenFile.getOriginalFilename();
        Path rutaCompleta = directorioCategoria.resolve(nombreUnico);

        Files.write(rutaCompleta, imagenFile.getBytes());
        return Paths.get(nombreCarpeta, nombreUnico).toString();
    }

    private void eliminarImagen(String nombreImagen) throws IOException {
        if (nombreImagen == null || nombreImagen.isEmpty()) return;
        
        Path rutaImagen = Paths.get(uploadDir).resolve(nombreImagen);
        Files.deleteIfExists(rutaImagen);
    }

    private void eliminarMultiplesImagenes(String imagenesStr) throws IOException {
        if (imagenesStr == null || imagenesStr.isEmpty()) return;
        
        String[] imagenes = imagenesStr.split(",");
        
        for (String imagen : imagenes) {
            if (imagen != null && !imagen.trim().isEmpty()) {
                eliminarImagen(imagen.trim());
            }
        }
    }
}