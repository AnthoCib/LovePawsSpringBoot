package com.lovepaws.app.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @PostMapping("/contacto")
    public String enviarContacto(@RequestParam String nombre,
                                 @RequestParam String correo,
                                 @RequestParam String asunto,
                                 @RequestParam String mensaje) {
        if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank() ||
                asunto == null || asunto.isBlank() || mensaje == null || mensaje.isBlank()) {
            return "redirect:/contacto?error";
        }
        return "redirect:/contacto?sent";
    }

}
