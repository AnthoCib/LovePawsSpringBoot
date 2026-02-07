package com.lovepaws.app.adopcion.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.security.UsuarioPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/adopciones")
@RequiredArgsConstructor
public class AdopcionApiController {

	private final SolicitudAdopcionService solicitudService;
	private final AdopcionService adopcionService;

	@PreAuthorize("hasRole('ADOPTANTE')")
	@PostMapping("/solicitar")
	public SolicitudAdopcion solicitar(@RequestBody SolicitudAdopcion solicitud, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		solicitud.setUsuario(principal.getUsuario());
		return solicitudService.createSolicitud(solicitud);
	}

	@PreAuthorize("hasAnyRole('ADOPTANTE','ADMIN')")
	@GetMapping("/usuario/{usuarioId}")
	public List<?> misAdopciones(@PathVariable Integer usuarioId, Authentication auth) {
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		if (!isAdmin && !principal.getUsuario().getId().equals(usuarioId)) {
			throw new IllegalArgumentException("No autorizado para consultar este historial");
		}
		return adopcionService.listarAdopcionesPorUsuario(usuarioId);
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/aprobar/{solicitudId}")
	public String aprobar(@PathVariable Integer solicitudId, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		adopcionService.aprobarSolicitud(solicitudId, principal.getUsuario().getId());
		return "Solicitud aprobada";
	}
}
