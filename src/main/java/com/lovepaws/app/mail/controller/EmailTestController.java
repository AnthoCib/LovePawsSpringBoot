package com.lovepaws.app.mail.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.adopcion.service.NotificacionEmailService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para pruebas manuales de correo.
 *
 * Se usa controlador HTTP (en lugar de CommandLineRunner) para:
 * - disparar pruebas bajo demanda,
 * - no ejecutar pruebas al arranque,
 * - recibir datos dinámicos por query o JSON.
 */
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailTestController {

    private final NotificacionEmailService notificacionEmailService;

    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    @PostMapping("/test-recepcion")
    public ResponseEntity<EmailTestResponse> testRecepcion(
            @RequestBody(required = false) EmailTestRequest body,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String nombreUsuario,
            @RequestParam(required = false) String nombreMascota) {

        String destino = resolve(body != null ? body.getCorreo() : null, correo);
        String usuario = resolve(body != null ? body.getNombreUsuario() : null, nombreUsuario);
        String mascota = resolve(body != null ? body.getNombreMascota() : null, nombreMascota);

        validateRequired(destino, "correo");
        validateRequired(usuario, "nombreUsuario");
        validateRequired(mascota, "nombreMascota");

        notificacionEmailService.enviarCorreoRecepcion(destino, usuario, mascota);
        return ResponseEntity.ok(EmailTestResponse.ok("Correo de recepción encolado correctamente"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    @PostMapping("/test-aprobacion")
    public ResponseEntity<EmailTestResponse> testAprobacion(
            @RequestBody(required = false) EmailTestRequest body,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String nombreUsuario,
            @RequestParam(required = false) String nombreMascota) {

        String destino = resolve(body != null ? body.getCorreo() : null, correo);
        String usuario = resolve(body != null ? body.getNombreUsuario() : null, nombreUsuario);
        String mascota = resolve(body != null ? body.getNombreMascota() : null, nombreMascota);

        validateRequired(destino, "correo");
        validateRequired(usuario, "nombreUsuario");
        validateRequired(mascota, "nombreMascota");

        notificacionEmailService.enviarCorreoAprobacion(destino, usuario, mascota);
        return ResponseEntity.ok(EmailTestResponse.ok("Correo de aprobación encolado correctamente"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    @PostMapping("/test-rechazo")
    public ResponseEntity<EmailTestResponse> testRechazo(
            @RequestBody(required = false) EmailTestRequest body,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String nombreUsuario,
            @RequestParam(required = false) String nombreMascota,
            @RequestParam(required = false) String motivo) {

        String destino = resolve(body != null ? body.getCorreo() : null, correo);
        String usuario = resolve(body != null ? body.getNombreUsuario() : null, nombreUsuario);
        String mascota = resolve(body != null ? body.getNombreMascota() : null, nombreMascota);
        String motivoFinal = resolve(body != null ? body.getMotivo() : null, motivo);

        validateRequired(destino, "correo");
        validateRequired(usuario, "nombreUsuario");
        validateRequired(mascota, "nombreMascota");
        validateRequired(motivoFinal, "motivo");

        notificacionEmailService.enviarCorreoRechazo(destino, usuario, mascota, motivoFinal);
        return ResponseEntity.ok(EmailTestResponse.ok("Correo de rechazo encolado correctamente"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    @PostMapping("/test-cancelacion")
    public ResponseEntity<EmailTestResponse> testCancelacion(
            @RequestBody(required = false) EmailTestRequest body,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String nombreUsuario,
            @RequestParam(required = false) String nombreMascota) {

        String destino = resolve(body != null ? body.getCorreo() : null, correo);
        String usuario = resolve(body != null ? body.getNombreUsuario() : null, nombreUsuario);
        String mascota = resolve(body != null ? body.getNombreMascota() : null, nombreMascota);

        validateRequired(destino, "correo");
        validateRequired(usuario, "nombreUsuario");
        validateRequired(mascota, "nombreMascota");

        notificacionEmailService.enviarCorreoCancelacion(destino, usuario, mascota);
        return ResponseEntity.ok(EmailTestResponse.ok("Correo de cancelación encolado correctamente"));
    }

    private String resolve(String bodyValue, String queryValue) {
        if (bodyValue != null && !bodyValue.isBlank()) {
            return bodyValue.trim();
        }
        return queryValue != null ? queryValue.trim() : null;
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' es obligatorio");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<EmailTestResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(EmailTestResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<EmailTestResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(EmailTestResponse.error("Error al procesar la prueba de correo: " + ex.getMessage()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailTestRequest {
        private String correo;
        private String nombreUsuario;
        private String nombreMascota;
        private String motivo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailTestResponse {
        private boolean ok;
        private String mensaje;
        private LocalDateTime timestamp;

        public static EmailTestResponse ok(String mensaje) {
            return new EmailTestResponse(true, mensaje, LocalDateTime.now());
        }

        public static EmailTestResponse error(String mensaje) {
            return new EmailTestResponse(false, mensaje, LocalDateTime.now());
        }
    }
}
