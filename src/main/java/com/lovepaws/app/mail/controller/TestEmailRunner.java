package com.lovepaws.app.mail.controller;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.lovepaws.app.mail.EmailService;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class TestEmailRunner implements CommandLineRunner {

    @Autowired
    private EmailService emailService;

    @Override
    public void run(String... args) throws Exception {
        String destinatario = "full.eagle.pnqt@protectsmail.net"; 
        String asunto;
        String contenido;
        
        // Ejemplo de datos de solicitud de adopción
        String nombreUsuario = "UsuarioPrueba";
        String nombreMascota = "Firulais";
        boolean aprobado = false; // Cambia a true si es aprobado
        String motivo = "No cumple con los requisitos de espacio y tiempo"; // motivo solo si es rechazo

        if (aprobado) {
            asunto = "¡Tu solicitud de adopción fue aprobada!";
            contenido = "<h2>Hola " + nombreUsuario + "!</h2>"
                    + "<p>¡Tu solicitud para adoptar a <b>" + nombreMascota + "</b> ha sido <b>aprobada</b>!</p>"
                    + "<p>Pronto nos pondremos en contacto para los siguientes pasos.</p>"
                    + "<p>¡Gracias por usar <i>Lovepaws</i>!</p>";
        } else {
            asunto = "Tu solicitud de adopción ha sido rechazada";
            contenido = "<h2>Hola " + nombreUsuario + "!</h2>"
                    + "<p>Lamentamos informarte que tu solicitud para adoptar a <b>" + nombreMascota + "</b> ha sido <b>rechazada</b>.</p>"
                    + "<p>Motivo: " + motivo + "</p>"

                    // Firma profesional
                    + "<div style='margin-top:20px; font-size:0.8em; color:#555;text-align:right;'>"   
                    + "<div style='text-align:right; line-height:1.3;'>"
                    + "------------------------------------------------------<br>"
                    + "Firmado por: <b>Juan Pérez Pérez</b><br>"
                    + "Admin - WebMaster<br>"
                    + "Empresa: <b>LovePaws, S.A.</b><br>"
                    + "ID de Certificado: <b>XxXyYyZzZ999</b><br>"
                    + "Fecha: <b>2025-05-14 10:00:00 CET</b><br>"
                    + "Verificado por: <b>Autoridad de Certificación</b></br>"
                    + "------------------------------------------------------<br>"
                    + "</div>"
                    + "<p>¡Gracias por usar <i>Lovepaws</i>!</p>"
                    + "</div>";
                    
        }

        /*emailService.enviarCorreo(destinatario, asunto, contenido);

        System.out.println("Correo de adopción enviado correctamente!");*/
    }
}

