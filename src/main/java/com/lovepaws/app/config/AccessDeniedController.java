package com.lovepaws.app.config;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        model.addAttribute("errorTitle", "Acceso denegado");
        model.addAttribute("errorMessage", "No tienes permisos para ver esta página.");
        return "error/general";
    }
}
