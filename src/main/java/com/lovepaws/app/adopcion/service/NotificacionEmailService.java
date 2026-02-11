package com.lovepaws.app.adopcion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lovepaws.app.mail.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionEmailService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionEmailService.class);

    private final EmailService emailService;

    @Async
    public void enviarCorreoRecepcion(String correoDestino, String nombreUsuario, String nombreMascota) {
        enviarCorreoSolicitud(correoDestino,
                "Hemos recibido tu solicitud de adopción",
                nombreUsuario,
                nombreMascota,
                "PENDIENTE",
                null);
    }

    @Async
    public void enviarCorreoAprobacion(String correoDestino, String nombreUsuario, String nombreMascota) {
        enviarCorreoSolicitud(correoDestino,
                "Tu solicitud de adopción ha sido aprobada",
                nombreUsuario,
                nombreMascota,
                "APROBADA",
                null);
    }

    @Async
    public void enviarCorreoRechazo(String correoDestino, String nombreUsuario, String nombreMascota, String motivo) {
        enviarCorreoSolicitud(correoDestino,
                "Tu solicitud de adopción ha sido rechazada",
                nombreUsuario,
                nombreMascota,
                "RECHAZADA",
                motivo);
    }

    @Async
    public void enviarCorreoCancelacion(String correoDestino, String nombreUsuario, String nombreMascota) {
        enviarCorreoSolicitud(correoDestino,
                "Solicitud de adopción cancelada",
                nombreUsuario,
                nombreMascota,
                "CANCELADA",
                null);
    }

    public void enviarCorreoSolicitud(String correoDestino,
                                      String asunto,
                                      String nombreUsuario,
                                      String nombreMascota,
                                      String estado,
                                      String motivo) {
        try {
            if (correoDestino == null || correoDestino.isBlank()) {
                logger.warn("No se envía correo: destinatario vacío");
                return;
            }

            String nombreSeguro = (nombreUsuario != null && !nombreUsuario.isBlank()) ? nombreUsuario : "Adoptante";
            String mascotaSegura = (nombreMascota != null && !nombreMascota.isBlank()) ? nombreMascota : "tu mascota";

            StringBuilder html = new StringBuilder();
            html.append("<p>Hola ").append(nombreSeguro).append(",</p>")
                    .append("<p>Tu solicitud para adoptar a <strong>").append(mascotaSegura)
                    .append("</strong> cambió al estado <strong>").append(estado).append("</strong>.</p>");

            if (motivo != null && !motivo.isBlank()) {
                html.append("<p><strong>Motivo:</strong> ").append(motivo).append("</p>");
            }

            html.append("<p>Gracias por confiar en LovePaws.</p>")
                    .append("<p>Equipo LovePaws</p>");

            emailService.enviarCorreo(correoDestino, asunto, html.toString());
            logger.info("Correo de solicitud enviado a {} con estado {}", correoDestino, estado);
        } catch (Exception e) {
            logger.error("Error enviando correo de solicitud a {}", correoDestino, e);
        }
    }
}
