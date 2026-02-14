package com.lovepaws.app.adopcion.controller;

import java.time.LocalDateTime;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.service.SeguimientoService;
import com.lovepaws.app.security.UsuarioPrincipal;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class DetalleAdopcionController {

    private final SeguimientoService seguimientoService;

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN','ADOPTANTE')")
    @GetMapping("/adopcion/{id}")
    public String verDetalleAdopcion(@PathVariable Integer id, Model model) {
        Adopcion adopcion = seguimientoService.obtenerAdopcionActivaAprobada(id);
        model.addAttribute("adopcion", adopcion);
        model.addAttribute("seguimientos", seguimientoService.listarPorAdopcion(id));
        model.addAttribute("respuestas", seguimientoService.listarRespuestasPorAdopcion(id));
        model.addAttribute("incumple8Semanas", seguimientoService.incumpleSeguimiento8Semanas(id));
        return "adopcion/detalle";
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PostMapping("/seguimiento/crear")
    public String crearSeguimiento(@RequestParam Integer adopcionId,
                                   @RequestParam LocalDateTime fechaVisita,
                                   @RequestParam(required = false) String observaciones,
                                   @RequestParam(required = false) String estadoId,
                                   Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        seguimientoService.crearSeguimientoCompleto(adopcionId, fechaVisita, observaciones, estadoId,
                principal.getUsuario().getId(), principal.getUsuario().getUsername());
        return "redirect:/adopcion/" + adopcionId + "?seguimientoCreado";
    }

    @PreAuthorize("hasRole('ADOPTANTE')")
    @PostMapping("/seguimiento/responder")
    public String responderSeguimiento(@RequestParam Integer seguimientoId,
                                       @RequestParam Integer adopcionId,
                                       @RequestParam String estadoSalud,
                                       @RequestParam String comportamiento,
                                       @RequestParam String alimentacion,
                                       @RequestParam(required = false) String comentarios,
                                       Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        seguimientoService.responderSeguimiento(seguimientoId, adopcionId, estadoSalud, comportamiento,
                alimentacion, comentarios, principal.getUsuario().getId(), principal.getUsuario().getUsername());
        return "redirect:/adopcion/" + adopcionId + "?respuestaCreada";
    }

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @PostMapping("/seguimiento/eliminar")
    public String eliminarSeguimiento(@RequestParam Integer seguimientoId,
                                      @RequestParam Integer adopcionId,
                                      Authentication authentication) {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        seguimientoService.eliminarSeguimientoSoft(seguimientoId, principal.getUsuario().getId(),
                principal.getUsuario().getUsername());
        return "redirect:/adopcion/" + adopcionId + "?seguimientoEliminado";
    }
}
