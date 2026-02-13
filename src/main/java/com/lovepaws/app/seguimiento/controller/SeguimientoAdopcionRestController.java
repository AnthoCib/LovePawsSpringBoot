package com.lovepaws.app.seguimiento.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.seguimiento.dto.CerrarSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.CrearSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.EscalarSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.ResponderSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoInteraccionResponse;
import com.lovepaws.app.seguimiento.dto.SeguimientoResponse;
import com.lovepaws.app.seguimiento.service.SeguimientoAdopcionService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seguimientos")
@RequiredArgsConstructor
public class SeguimientoAdopcionRestController {

    private final SeguimientoAdopcionService seguimientoService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public SeguimientoResponse crear(@Valid @RequestBody CrearSeguimientoRequest request,
                                     @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoService.crearSeguimiento(request, principal.getUsuario().getId());
    }

    @PostMapping("/{seguimientoId}/respuestas")
    @PreAuthorize("hasRole('ADOPTANTE')")
    public SeguimientoResponse responder(@PathVariable Long seguimientoId,
                                         @Valid @RequestBody ResponderSeguimientoRequest request,
                                         @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoService.responderSeguimiento(seguimientoId, request, principal.getUsuario().getId());
    }

    @PostMapping("/{seguimientoId}/cerrar")
    @PreAuthorize("hasRole('GESTOR')")
    public SeguimientoResponse cerrar(@PathVariable Long seguimientoId,
                                      @Valid @RequestBody CerrarSeguimientoRequest request,
                                      @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoService.cerrarSeguimiento(seguimientoId, request, principal.getUsuario().getId());
    }

    @PostMapping("/{seguimientoId}/escalar")
    @PreAuthorize("hasRole('GESTOR')")
    public SeguimientoResponse escalar(@PathVariable Long seguimientoId,
                                       @Valid @RequestBody EscalarSeguimientoRequest request,
                                       @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoService.escalarSeguimiento(seguimientoId, request, principal.getUsuario().getId());
    }

    @GetMapping("/{seguimientoId}")
    @PreAuthorize("hasAnyRole('GESTOR','ADOPTANTE','ADMIN')")
    public SeguimientoResponse detalle(@PathVariable Long seguimientoId,
                                       @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoService.obtenerSeguimiento(
                seguimientoId,
                principal.getUsuario().getId(),
                esGestorOAdmin(principal));
    }

    @GetMapping("/{seguimientoId}/historial")
    @PreAuthorize("hasAnyRole('GESTOR','ADOPTANTE','ADMIN')")
    public List<SeguimientoInteraccionResponse> historial(@PathVariable Long seguimientoId,
                                                           @AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoService.obtenerHistorial(
                seguimientoId,
                principal.getUsuario().getId(),
                esGestorOAdmin(principal));
    }

    @GetMapping("/mis")
    @PreAuthorize("hasRole('ADOPTANTE')")
    public List<SeguimientoResponse> misSeguimientos(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return seguimientoService.listarMisSeguimientos(principal.getUsuario().getId());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    public List<SeguimientoResponse> listarGestion() {
        return seguimientoService.listarSeguimientosGestor();
    }

    private boolean esGestorOAdmin(UsuarioPrincipal principal) {
        return principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> "ROLE_GESTOR".equals(a) || "ROLE_ADMIN".equals(a));
    }
}
