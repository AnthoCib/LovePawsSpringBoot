package com.lovepaws.app.adopcion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seguimientos-post-adopcion")
@RequiredArgsConstructor
@Validated
public class SeguimientoPostAdopcionApiController {

    private final SeguimientoPostAdopcionApiService seguimientoApiService;

    // Crea seguimiento post-adopción para adopciones confirmadas.
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeguimientoPostAdopcionResponseDTO crear(@Valid @RequestBody SeguimientoPostAdopcionRequestDTO request,
                                                    Authentication auth) {
        Integer gestorId = null;
        if (auth != null && auth.getPrincipal() instanceof UsuarioPrincipal principal) {
            gestorId = principal.getUsuario().getId();
        }
        return seguimientoApiService.crearSeguimiento(request, gestorId);
    }

    // Lista seguimientos filtrados por estadoMascota de tracking (opcional).
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @GetMapping
    public List<SeguimientoPostAdopcionResponseDTO> listar(
            @RequestParam(required = false) SeguimientoPostAdopcion.EstadoMascotaSeguimiento estadoMascota) {
        return seguimientoApiService.listarSeguimientos(estadoMascota);
    }

    // Actualiza seguimiento existente (fecha, notas, estado, adopcion asociada).
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PatchMapping("/{seguimientoId}")
    public SeguimientoPostAdopcionResponseDTO actualizar(@PathVariable Integer seguimientoId,
                                                         @Valid @RequestBody SeguimientoPostAdopcionRequestDTO request) {
        return seguimientoApiService.actualizarSeguimiento(seguimientoId, request);
    }
}
