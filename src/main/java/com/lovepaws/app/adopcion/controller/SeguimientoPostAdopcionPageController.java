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

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestor/seguimientos-post-adopcion")
@RequiredArgsConstructor
public class SeguimientoPostAdopcionPageController {

    private static final String TIPO_MASCOTA = "MASCOTA";
    private static final String TIPO_PROCESO = "PROCESO";

    private final SeguimientoPostAdopcionApiService seguimientoApiService;

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @GetMapping
    public String pagina(@RequestParam(required = false) EstadoMascotaTracking estado,
                         @RequestParam(defaultValue = TIPO_MASCOTA) String tipo,
                         Model model) {
        String tipoNormalizado = TIPO_PROCESO.equalsIgnoreCase(tipo) ? TIPO_PROCESO : TIPO_MASCOTA;
        List<EstadoMascotaTracking> estadosDisponibles = EstadoMascotaTracking.valoresPorTipo(tipoNormalizado);

        EstadoMascotaTracking estadoFiltro = (estado != null && estado.esTipo(tipoNormalizado)) ? estado : null;

        model.addAttribute("seguimientos", seguimientoApiService.listarSeguimientos(estadoFiltro));
        model.addAttribute("estadoSeleccionado", estadoFiltro);
        model.addAttribute("estados", estadosDisponibles);
        model.addAttribute("tipoSeleccionado", tipoNormalizado);
        return "adopcion/seguimiento-post-adopcion";
    }
}
