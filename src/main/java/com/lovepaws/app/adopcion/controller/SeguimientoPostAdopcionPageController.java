package com.lovepaws.app.adopcion.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.seguimiento.repository.EstadoSeguimientoRepository;
import com.lovepaws.app.seguimiento.service.SeguimientoPostAdopcionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestor/seguimientos-post-adopcion")
@RequiredArgsConstructor
public class SeguimientoPostAdopcionPageController {

    private final SeguimientoPostAdopcionService seguimientoService;
    private final EstadoSeguimientoRepository estadoSeguimientoRepository;

    @PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
    @GetMapping
    public String pagina(@RequestParam(required = false) String estado,
                         Model model) {

    	EstadoSeguimiento estadoEntity = null;

    	if (estado != null && !estado.isBlank()) {
    	    estadoEntity = estadoSeguimientoRepository
    	                        .findById(estado)
    	                        .orElse(null);
    	}

    	List<SeguimientoPostAdopcion> lista =
    	        Optional.ofNullable(seguimientoService
    	                .listarSeguimientos(estadoEntity))
    	                .orElse(List.of());

        if (lista == null) {
            lista = List.of();
        }

        model.addAttribute("seguimientos", lista);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", estadoSeguimientoRepository.findAll());

        model.addAttribute("resultadosSeguimiento",
                Optional.ofNullable(seguimientoService.listarResultados())
                        .orElse(List.of()));

        return "adopcion/seguimiento-post-adopcion";
    }
}