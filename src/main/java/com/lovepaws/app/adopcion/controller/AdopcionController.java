package com.lovepaws.app.adopcion.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import com.lovepaws.app.security.UsuarioPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.mascota.service.MascotaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/adopcion")
@RequiredArgsConstructor
public class AdopcionController {

	private final SolicitudAdopcionService solicitudService;
	private final AdopcionService adopcionService;
	private final MascotaService mascotaService;

	// Adoptante: crear solicitud para una mascota
	@PreAuthorize("hasRole('ADOPTANTE')")
	@PostMapping("/solicitar")

	public String solicitarAdopcion(@ModelAttribute("solicitud") @Validated SolicitudAdopcion solicitud,
			BindingResult br, Authentication auth) {
		if (solicitud.getMascota() == null) {
			return "redirect:/mascotas?error";
		}
		if (br.hasErrors()) {
			return "redirect:/mascotas/" + solicitud.getMascota().getId() + "?error";
		}
		EstadoAdopcion estado = new EstadoAdopcion();
		estado.setId("PENDIENTE");
		solicitud.setEstado(estado);

		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		solicitud.setUsuario(principal.getUsuario());
		try {
			solicitudService.createSolicitud(solicitud);
		} catch (DataIntegrityViolationException ex) {
			return "redirect:/mascotas?duplicate=true";
		}
		return "redirect:/adopcion/mis-adopciones?created";
	}

	// Gestor: ver solicitudes pendientes por mascota
	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@GetMapping("/gestor/solicitudes/{mascotaId}")
	public String verSolicitudesPorMascota(@PathVariable Integer mascotaId, Model model) {
		model.addAttribute("solicitudes", solicitudService.listarSolicitudesPorMascota(mascotaId));
		model.addAttribute("mascota", mascotaService.findMascotaById(mascotaId).orElse(null));
		return "adopcion/solicitudes";
	}

	// Gestor: aprobar solicitud (usa AdopcionService.aprobarSolicitud -
	// transaccional)
	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/aprobar/{solicitudId}")
	public String aprobarSolicitud(@PathVariable Integer solicitudId, Authentication auth) {

		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Integer gestorId = principal.getUsuario().getId();
		// Se obtiene gestorId del session con Authentication;
		adopcionService.aprobarSolicitud(solicitudId, gestorId);
		Integer mascotaId = solicitudService.findSolicitudById(solicitudId)
				.map(s -> s.getMascota().getId())
				.orElse(null);
		if (mascotaId == null) {
			return "redirect:/gestor/mascotas?aprobada";
		}
		return "redirect:/adopcion/gestor/solicitudes/" + mascotaId + "?aprobada";
	}

	// Adoptante: ver mis adopciones
	@PreAuthorize("hasRole('ADOPTANTE')")
	@GetMapping("/mis-adopciones")
	public String misAdopciones(Model model, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Integer usuarioId = principal.getUsuario().getId();
		model.addAttribute("adopciones", adopcionService.listarAdopcionesPorUsuario(usuarioId));
		return "adopcion/mis-adopciones";
	}

}
