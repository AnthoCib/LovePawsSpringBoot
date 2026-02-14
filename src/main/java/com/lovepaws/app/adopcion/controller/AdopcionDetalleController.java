package com.lovepaws.app.adopcion.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoRequestDTO;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.RespuestaSeguimientoAdopcionService;
import com.lovepaws.app.adopcion.service.SeguimientoService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.repository.EstadoMascotaRepository;
import com.lovepaws.app.security.UsuarioPrincipal;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/adopcion")
@RequiredArgsConstructor
public class AdopcionDetalleController {

    private final AdopcionService adopcionService;
    private final SeguimientoService seguimientoService;
    private final RespuestaSeguimientoAdopcionService respuestaService;
    private final AdopcionRepository adopcionRepository;
    private final EstadoMascotaRepository estadoMascotaRepository;

    @PreAuthorize("hasAnyRole('ADOPTANTE','GESTOR','ADMIN')")
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Integer id, Authentication auth, Model model) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        Adopcion adopcion = adopcionService.findAdopcionById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));

        boolean esGestorAdmin = tieneRol(principal, "GESTOR") || tieneRol(principal, "ADMIN");
        boolean esAdoptanteOwner = adopcion.getUsuarioAdoptante() != null
                && adopcion.getUsuarioAdoptante().getId().equals(principal.getUsuario().getId());

        if (!esGestorAdmin && !esAdoptanteOwner) {
            return "redirect:/adopcion/mis-adopciones?error=forbidden";
        }

        boolean incumple8Semanas = adopcionRepository.existsIncumplimientoSeguimiento8SemanasByAdopcionId(id);

        model.addAttribute("adopcion", adopcion);
        model.addAttribute("seguimientos", seguimientoService.listarPorAdopcion(id));
        model.addAttribute("respuestas", respuestaService.listarPorAdopcion(id));
        model.addAttribute("incumple8Semanas", incumple8Semanas);
        model.addAttribute("estadosMascota", estadoMascotaRepository.findAll());
        model.addAttribute("puedeGestionar", esGestorAdmin);
        model.addAttribute("puedeResponder", esAdoptanteOwner);
        return "adopcion/detalle";
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PostMapping("/seguimiento/crear")
    public String crearSeguimiento(@RequestParam Integer adopcionId,
                                   @RequestParam String fechaVisita,
                                   @RequestParam(required = false) String observaciones,
                                   @RequestParam(required = false) String estadoId,
                                   Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        LocalDateTime fecha;
        try {
            fecha = LocalDateTime.parse(fechaVisita, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (DateTimeParseException ex) {
            return "redirect:/adopcion/" + adopcionId + "?error=fecha";
        }

        SeguimientoAdopcion seguimiento = new SeguimientoAdopcion();
        Adopcion adopcion = new Adopcion();
        adopcion.setId(adopcionId);
        seguimiento.setAdopcion(adopcion);
        seguimiento.setFechaVisita(fecha);
        seguimiento.setObservaciones(observaciones != null ? observaciones.trim() : null);
        seguimiento.setUsuarioCreacion(principal.getUsuario());
        seguimiento.setActivo(Boolean.TRUE);
        if (estadoId != null && !estadoId.isBlank()) {
            EstadoMascota estado = new EstadoMascota();
            estado.setId(estadoId);
            seguimiento.setEstado(estado);
        }

        seguimientoService.createSeguimiento(seguimiento, principal.getUsuario().getId(), principal.getUsuario().getUsername());
        return "redirect:/adopcion/" + adopcionId + "?seguimientoCreado";
    }

    @PreAuthorize("hasRole('ADOPTANTE')")
    @PostMapping("/seguimiento/responder")
    public String responderSeguimiento(@RequestParam Integer adopcionId,
                                       @RequestParam Integer seguimientoId,
                                       @RequestParam String estadoSalud,
                                       @RequestParam String comportamiento,
                                       @RequestParam String alimentacion,
                                       @RequestParam(required = false) String comentarios,
                                       @RequestParam(required = false) MultipartFile foto,
                                       Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();

        Adopcion adopcion = adopcionService.findAdopcionById(adopcionId)
                .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));

        if (adopcion.getUsuarioAdoptante() == null || !adopcion.getUsuarioAdoptante().getId().equals(principal.getUsuario().getId())) {
            return "redirect:/adopcion/mis-adopciones?error=forbidden";
        }

        SeguimientoAdopcion seguimiento = seguimientoService.findById(seguimientoId)
                .orElseThrow(() -> new IllegalArgumentException("Seguimiento no encontrado"));
        if (seguimiento.getAdopcion() == null || !seguimiento.getAdopcion().getId().equals(adopcionId)) {
            return "redirect:/adopcion/" + adopcionId + "?error=seguimiento";
        }

        RespuestaSeguimientoRequestDTO request = new RespuestaSeguimientoRequestDTO();
        request.setAdopcionId(adopcionId);
        request.setSeguimientoId(seguimientoId);
        request.setEstadoSalud(estadoSalud);
        request.setComportamiento(comportamiento);
        request.setAlimentacion(alimentacion);
        request.setComentarios(comentarios);

        respuestaService.registrarRespuesta(request, foto, principal.getUsuario().getId(), principal.getUsuario().getUsername());
        return "redirect:/adopcion/" + adopcionId + "?respuestaCreada";
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PostMapping("/seguimiento/{seguimientoId}/eliminar")
    public String eliminarSeguimiento(@PathVariable Integer seguimientoId,
                                      @RequestParam Integer adopcionId,
                                      Authentication auth) {
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        seguimientoService.eliminarLogico(seguimientoId, principal.getUsuario().getId(), principal.getUsuario().getUsername());
        return "redirect:/adopcion/" + adopcionId + "?seguimientoEliminado";
    }

    private boolean tieneRol(UsuarioPrincipal principal, String rolBuscado) {
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rolBuscado));
    }
}
