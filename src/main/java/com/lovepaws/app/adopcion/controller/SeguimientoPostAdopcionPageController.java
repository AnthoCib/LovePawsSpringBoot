package com.lovepaws.app.adopcion.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SeguimientoPostAdopcionPageController {

    private static final String TIPO_MASCOTA = "MASCOTA";
    private static final String TIPO_PROCESO = "PROCESO";

    private final SeguimientoAdopcionRepository seguimientoRepository;

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @GetMapping("/gestor/seguimientos-post-adopcion")
    public String pagina(@RequestParam(defaultValue = TIPO_MASCOTA) String tipo,
                         @RequestParam(required = false) String estado,
                         Model model) {
        String tipoSeleccionado = TIPO_PROCESO.equalsIgnoreCase(tipo) ? TIPO_PROCESO : TIPO_MASCOTA;
        String estadoSeleccionado = (estado != null && !estado.isBlank()) ? estado.trim().toUpperCase() : null;

        List<SeguimientoAdopcion> seguimientos;
        if (estadoSeleccionado == null) {
            seguimientos = seguimientoRepository.findAll();
        } else if (TIPO_PROCESO.equals(tipoSeleccionado)) {
            seguimientos = seguimientoRepository.findByEstadoProceso_Id(estadoSeleccionado);
        } else {
            seguimientos = seguimientoRepository.findByEstadoMascota_Id(estadoSeleccionado);
        }

        model.addAttribute("seguimientos", seguimientos);
        model.addAttribute("tipoSeleccionado", tipoSeleccionado);
        model.addAttribute("estadoSeleccionado", estadoSeleccionado);
        model.addAttribute("estados", EstadoMascotaTracking.valoresPorTipo(tipoSeleccionado));
        return "adopcion/seguimiento-post-adopcion";
    }
}
