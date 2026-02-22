package com.lovepaws.app.seguimiento.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.seguimiento.dto.RespuestaSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoCreateRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoResponse;
import com.lovepaws.app.seguimiento.service.SeguimientoPostAdopcionService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seguimientos")
@RequiredArgsConstructor
public class SeguimientoController {

    private final SeguimientoPostAdopcionService seguimientoPostAdopcionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public SeguimientoResponse crear(@Valid @RequestBody SeguimientoCreateRequest request,
                                     @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoPostAdopcionService.crearSeguimiento(request, principal.getUsuario().getId());
    }

    @PostMapping("/{seguimientoId}/respuestas")
    @PreAuthorize("hasRole('ADOPTANTE')")
    public SeguimientoResponse responder(@PathVariable Integer seguimientoId,
                                         @Valid @RequestBody RespuestaSeguimientoRequest request,
                                         @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoPostAdopcionService.responderSeguimiento(seguimientoId, request, principal.getUsuario().getId());
    }

    @PostMapping("/{seguimientoId}/cerrar")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public SeguimientoResponse cerrar(@PathVariable Integer seguimientoId,
                                      @RequestParam String comentario,
                                      @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoPostAdopcionService.cerrarSeguimiento(seguimientoId, comentario, principal.getUsuario().getId());
    }

    @PostMapping("/{seguimientoId}/escalar")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public SeguimientoResponse escalar(@PathVariable Integer seguimientoId,
                                       @RequestParam String motivo,
                                       @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoPostAdopcionService.escalarSeguimiento(seguimientoId, motivo, principal.getUsuario().getId());
    }

    @DeleteMapping("/{seguimientoId}")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public void eliminar(@PathVariable Integer seguimientoId,
                         @AuthenticationPrincipal UsuarioPrincipal principal) {
        seguimientoPostAdopcionService.eliminarLogico(seguimientoId, principal.getUsuario().getId());
    }

    @GetMapping("/{seguimientoId}")
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN','ADOPTANTE')")
    public SeguimientoResponse detalle(@PathVariable Integer seguimientoId,
                                       @AuthenticationPrincipal UsuarioPrincipal principal) {
        boolean gestor = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> "ROLE_GESTOR".equals(a) || "ROLE_ADMIN".equals(a));
        return seguimientoPostAdopcionService.obtenerDetalle(seguimientoId, principal.getUsuario().getId(), gestor);
    }

    @GetMapping("/mis")
    @PreAuthorize("hasRole('ADOPTANTE')")
    public List<SeguimientoResponse> mis(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoPostAdopcionService.listarMisSeguimientos(principal.getUsuario().getId());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<SeguimientoResponse> listarGestion() {
        return seguimientoPostAdopcionService.listarSeguimientosGestion();
    }
}
