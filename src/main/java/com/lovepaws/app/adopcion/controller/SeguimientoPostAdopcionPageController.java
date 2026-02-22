package com.lovepaws.app.adopcion.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.seguimiento.domain.ResultadoSeguimiento;
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
        // Seguimientos filtrados por estado
        model.addAttribute("seguimientos", seguimientoService.listarSeguimientos(estado));
        model.addAttribute("estadoSeleccionado", estado);

        // Estados físicos de la mascota (tracking)
        model.addAttribute("estados", EstadoMascotaTracking.values());

        // Resultados posibles de seguimiento (desde el servicio)
        model.addAttribute("resultadosSeguimiento", seguimientoService.listarResultados());

      
        return "adopcion/seguimiento-post-adopcion";
    }
}
