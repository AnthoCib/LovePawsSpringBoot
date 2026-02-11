package com.lovepaws.app.adopcion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.mail.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionEmailService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionEmailService.class);

    private final EmailService emailService;

    @Async
    public void enviarCorreoRecepcion(SolicitudAdopcion solicitud) {
        enviarCorreoSolicitud(solicitud,
                "Hemos recibido tu solicitud de adopción",
                "PENDIENTE",
                null);
    }

    @Async
    public void enviarCorreoAprobacion(SolicitudAdopcion solicitud) {
        enviarCorreoSolicitud(solicitud,
                "Tu solicitud de adopción ha sido aprobada",
                "APROBADA",
                null);
    }

    @Async
    public void enviarCorreoRechazo(SolicitudAdopcion solicitud, String motivo) {
        enviarCorreoSolicitud(solicitud,
                "Tu solicitud de adopción ha sido rechazada",
                "RECHAZADA",
                motivo);
    }

    @Async
    public void enviarCorreoCancelacion(SolicitudAdopcion solicitud) {
        enviarCorreoSolicitud(solicitud,
                "Solicitud de adopción cancelada",
                "CANCELADA",
                null);
    }

    public void enviarCorreoSolicitud(SolicitudAdopcion solicitud,
                                      String asunto,
                                      String estado,
                                      String motivo) {
        try {
            if (solicitud == null || solicitud.getUsuario() == null || solicitud.getMascota() == null) {
                logger.warn("No se envía correo: solicitud incompleta");
                return;
            }

            String correo = solicitud.getUsuario().getCorreo();
            if (correo == null || correo.isBlank()) {
                logger.warn("No se envía correo: usuario sin correo para solicitud {}", solicitud.getId());
                return;
            }

            String nombreUsuario = solicitud.getUsuario().getNombre() != null
                    ? solicitud.getUsuario().getNombre()
                    : "Adoptante";
            String nombreMascota = solicitud.getMascota().getNombre() != null
                    ? solicitud.getMascota().getNombre()
                    : "tu mascota";

            StringBuilder html = new StringBuilder();
            html.append("<p>Hola ").append(nombreUsuario).append(",</p>")
                    .append("<p>Tu solicitud para adoptar a <strong>").append(nombreMascota)
                    .append("</strong> cambió al estado <strong>").append(estado).append("</strong>.</p>");

            if (motivo != null && !motivo.isBlank()) {
                html.append("<p><strong>Motivo:</strong> ").append(motivo).append("</p>");
            }

            html.append("<p>Gracias por confiar en LovePaws.</p>")
                    .append("<p>Equipo LovePaws</p>");

            emailService.enviarCorreo(correo, asunto, html.toString());
            logger.info("Correo de solicitud enviado a {} con estado {}", correo, estado);
        } catch (Exception e) {
            logger.error("Error enviando correo de solicitud para ID {}", solicitud != null ? solicitud.getId() : null, e);
        }
    }
}
