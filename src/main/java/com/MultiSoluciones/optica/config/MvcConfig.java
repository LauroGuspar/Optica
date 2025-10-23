package com.MultiSoluciones.optica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.upload-dir-empresa-logo}")
    private String uploadDirEmpresaLogo;
    @Value("${file.upload-dir-empresa-sidebar}")
    private String uploadDirEmpresaSidebar;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/productos/**")
                .addResourceLocations("file:" + uploadDir);
                
        registry.addResourceHandler("/empresa-logos/**")
                .addResourceLocations("file:" + uploadDirEmpresaLogo);

        registry.addResourceHandler("/empresa-sidebars/**")
                .addResourceLocations("file:" + uploadDirEmpresaSidebar);
    }
}