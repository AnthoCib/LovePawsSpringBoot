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
        boolean valido = nombre != null && nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]{2,80}$")
                && correo != null && correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                && asunto != null && asunto.length() >= 4 && asunto.length() <= 120
                && mensaje != null && mensaje.length() >= 10 && mensaje.length() <= 1000;

        if (!valido) {
            return "redirect:/contacto?error";
        }
        return "redirect:/contacto?sent";
    }

}
