package com.lovepaws.app.adopcion.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestor/seguimientos-post-adopcion")
@RequiredArgsConstructor
public class SeguimientoPostAdopcionPageController {

    private final SeguimientoPostAdopcionApiService seguimientoApiService;

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @GetMapping
    public String pagina(@RequestParam(required = false) EstadoMascotaTracking estado,
                         Model model) {
        model.addAttribute("seguimientos", seguimientoApiService.listarSeguimientos(estado, null));
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", EstadoMascotaTracking.values());
        return "adopcion/seguimiento-post-adopcion";
    }
}
