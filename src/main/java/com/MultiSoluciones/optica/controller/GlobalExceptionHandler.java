package com.MultiSoluciones.optica.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.MultiSoluciones.optica.model.Empresa;
import com.MultiSoluciones.optica.service.EmpresaService;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final EmpresaService empresaService;

    public GlobalExceptionHandler(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @ModelAttribute("empresaGlobal")
    public Empresa addEmpresaToModel() {
        return empresaService.getEmpresa().orElse(null);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException(TypeMismatchException ex) {

        logger.warn("Se detectó un intento de acceder a una URL con un tipo de dato incorrecto. " + "Valor: '{}', Tipo requerido: '{}'. Redirigiendo a la página de inicio.",
                ex.getValue(), ex.getRequiredType());

        return "redirect:/";
    }
}