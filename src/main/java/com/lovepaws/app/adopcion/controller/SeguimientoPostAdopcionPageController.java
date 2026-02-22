package com.lovepaws.app.adopcion.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.seguimiento.service.SeguimientoPostAdopcionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestor/seguimientos-post-adopcion")
@RequiredArgsConstructor
public class SeguimientoPostAdopcionPageController {

    private final SeguimientoPostAdopcionService seguimientoService;

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @GetMapping
    public String pagina(@RequestParam(required = false) EstadoSeguimiento estado,
                         Model model) {
        model.addAttribute("seguimientos", seguimientoService.listarSeguimientos(estado));
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", EstadoMascotaTracking.values());
        
        model.addAttribute("resultadosSeguimiento", seguimientoService.listarResultados());
        return "adopcion/seguimiento-post-adopcion";
    }
}
