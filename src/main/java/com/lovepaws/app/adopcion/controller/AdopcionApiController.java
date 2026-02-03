package com.lovepaws.app.adopcion.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/adopciones")
@RequiredArgsConstructor
public class AdopcionApiController {

	private final SolicitudAdopcionService solicitudService;
	private final AdopcionService adopcionService;

	// Crear solicitud
	@PostMapping("/solicitar")
	public SolicitudAdopcion solicitar(@RequestBody SolicitudAdopcion solicitud) {

		return solicitudService.createSolicitud(solicitud);
	}

	// Ver adopciones de usuario
	@GetMapping("/usuario/{usuarioId}")
	public List<?> misAdopciones(@PathVariable Integer usuarioId) {
		return adopcionService.listarAdopcionesPorUsuario(usuarioId);
	}

	// Aprobar solicitud
	@PostMapping("/aprobar/{solicitudId}")
	public String aprobar(@PathVariable Integer solicitudId, @RequestParam Integer gestorId) {

		adopcionService.aprobarSolicitud(solicitudId, gestorId);
		return "Solicitud aprobada";
	}
}
