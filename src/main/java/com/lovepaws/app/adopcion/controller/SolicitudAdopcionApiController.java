package com.lovepaws.app.adopcion.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.adopcion.dto.CambioEstadoSolicitudRequestDTO;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/solicitudes-adopcion")
@RequiredArgsConstructor
@Validated
public class SolicitudAdopcionApiController {

    private final SolicitudAdopcionService solicitudAdopcionService;

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PatchMapping("/{solicitudId}/estado")
    public SolicitudAdopcion cambiarEstado(@PathVariable Integer solicitudId,
                                           @Valid @RequestBody CambioEstadoSolicitudRequestDTO request,
                                           Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        Integer gestorId = principal.getUsuario().getId();

        String accion = request.getAccion().trim().toUpperCase();
        return switch (accion) {
            case "APROBAR" -> solicitudAdopcionService.aprobarSolicitud(solicitudId, gestorId);
            case "RECHAZAR" -> solicitudAdopcionService.rechazarSolicitud(solicitudId, gestorId, request.getMotivo());
            default -> throw new IllegalArgumentException("Acción no permitida. Usa APROBAR o RECHAZAR");
        };
    }
}
