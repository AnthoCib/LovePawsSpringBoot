package com.lovepaws.app.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, RuntimeException.class})
    public String handleKnownExceptions(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Ocurrió un problema");
        model.addAttribute("errorMessage", ex.getMessage() == null || ex.getMessage().isBlank()
                ? "No se pudo completar la operación solicitada."
                : ex.getMessage());
        return "error/general";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Error inesperado");
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado. Intenta nuevamente.");
        return "error/general";
    }
}
