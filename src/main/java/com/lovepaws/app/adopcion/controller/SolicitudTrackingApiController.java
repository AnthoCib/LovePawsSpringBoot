package com.lovepaws.app.adopcion.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.adopcion.dto.ActualizarEstadoSolicitudDTO;
import com.lovepaws.app.adopcion.dto.SolicitudTrackingResponseDTO;
import com.lovepaws.app.adopcion.service.SolicitudTrackingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/solicitudes-tracking")
@RequiredArgsConstructor
@Validated
public class SolicitudTrackingApiController {

    private final SolicitudTrackingService solicitudTrackingService;

    // Endpoint para listar solicitudes por estado (o todas si no se envía filtro).
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @GetMapping
    public List<SolicitudTrackingResponseDTO> listarPorEstado(@RequestParam(required = false) String estado) {
        return solicitudTrackingService.listarPorEstado(estado);
    }

    // Endpoint para actualizar estado de la solicitud desde panel de gestión.
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PatchMapping("/{solicitudId}/estado")
    public SolicitudTrackingResponseDTO actualizarEstado(@PathVariable Integer solicitudId,
                                                         @Valid @RequestBody ActualizarEstadoSolicitudDTO request) {
        return solicitudTrackingService.actualizarEstado(solicitudId, request.getEstado(), request.getComentario());
    }
}
