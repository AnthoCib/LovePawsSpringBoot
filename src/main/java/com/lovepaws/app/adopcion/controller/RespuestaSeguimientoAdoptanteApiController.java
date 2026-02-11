package com.lovepaws.app.adopcion.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoRequestDTO;
import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoResponseDTO;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.RespuestaSeguimientoAdoptanteService;
import com.lovepaws.app.security.UsuarioPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/respuestas-seguimiento")
@RequiredArgsConstructor
@Validated
public class RespuestaSeguimientoAdoptanteApiController {

    private final RespuestaSeguimientoAdoptanteService respuestaService;
    private final AdopcionService adopcionService;

    // Lista respuestas por adopción para seguimiento del adoptante o revisión del gestor.
    @PreAuthorize("hasAnyRole('ADOPTANTE','GESTOR','ADMIN')")
    @GetMapping("/adopcion/{adopcionId}")
    public List<RespuestaSeguimientoResponseDTO> listarPorAdopcion(@PathVariable Integer adopcionId,
                                                                   Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        boolean esGestorOAdmin = auth.getAuthorities().stream().anyMatch(a ->
                "ROLE_GESTOR".equals(a.getAuthority()) || "ROLE_ADMIN".equals(a.getAuthority()));

        if (!esGestorOAdmin) {
            Integer adoptanteId = adopcionService.findAdopcionById(adopcionId)
                    .map(a -> a.getUsuarioAdoptante() != null ? a.getUsuarioAdoptante().getId() : null)
                    .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));
            if (adoptanteId == null || !adoptanteId.equals(principal.getUsuario().getId())) {
                throw new IllegalArgumentException("No autorizado para ver respuestas de esta adopción");
            }
        }

        return respuestaService.listarPorAdopcion(adopcionId);
    }

    // Registro de respuesta del adoptante con foto opcional.
    @PreAuthorize("hasAnyRole('ADOPTANTE','GESTOR','ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    public RespuestaSeguimientoResponseDTO registrar(@Valid @ModelAttribute RespuestaSeguimientoRequestDTO request,
                                                     @RequestParam(required = false) MultipartFile foto,
                                                     Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        return respuestaService.registrarRespuesta(request, foto, principal.getUsuario().getId(),
                principal.getUsuario().getUsername());
    }

    // Revisión por gestor.
    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PatchMapping("/{respuestaId}/revisado")
    public RespuestaSeguimientoResponseDTO marcarRevisado(@PathVariable Integer respuestaId,
                                                          @RequestParam boolean revisado,
                                                          Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        return respuestaService.marcarRevisado(respuestaId, revisado, principal.getUsuario().getId(),
                principal.getUsuario().getUsername());
    }
}
