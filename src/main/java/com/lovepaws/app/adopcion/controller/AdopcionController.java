package com.lovepaws.app.adopcion.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.service.MascotaService;
import com.lovepaws.app.security.UsuarioPrincipal;
import com.lovepaws.app.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/adopcion")
@RequiredArgsConstructor
public class AdopcionController {

	private final SolicitudAdopcionService solicitudService;
	private final AdopcionService adopcionService;
	private final MascotaService mascotaService;

	@GetMapping
	public String flujoAdopcion() {
		return "adopcion/flujo";
	}

	@PreAuthorize("hasRole('ADOPTANTE')")
	@PostMapping("/solicitar")
	public String solicitarAdopcion(@ModelAttribute("solicitud") @Validated SolicitudAdopcion solicitud,
			BindingResult br, Authentication auth) {

		if (solicitud.getMascota() == null) {
			return "redirect:/mascotas?error=mascota";
		}
		if (br.hasErrors()) {
			return "redirect:/mascotas/" + solicitud.getMascota().getId() + "?error=form";
		}

		Mascota mascota = mascotaService.findMascotaById(solicitud.getMascota().getId())
				.orElse(null);
		if (mascota == null || mascota.getEstado() == null || !"DISPONIBLE".equalsIgnoreCase(mascota.getEstado().getId())) {
			return "redirect:/mascotas?error=estado-mascota";
		}

		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Usuario usuario = principal.getUsuario();

		if (usuario.getNombre() == null || usuario.getNombre().isBlank() || usuario.getCorreo() == null
				|| !usuario.getCorreo().contains("@") || usuario.getTelefono() == null || usuario.getTelefono().isBlank()
				|| usuario.getDireccion() == null || usuario.getDireccion().isBlank()) {
			return "redirect:/usuarios/perfil?error=incompleto";
		}

		if (solicitud.getPqAdoptar() == null || solicitud.getPqAdoptar().isBlank()) {
			return "redirect:/mascotas/" + mascota.getId() + "?error=motivo";
		}

		boolean mascotaConSolicitudActiva = solicitudService.listarSolicitudesPorMascota(mascota.getId()).stream()
				.anyMatch(s -> s.getEstado() != null && "PENDIENTE".equalsIgnoreCase(s.getEstado().getId()));
		if (mascotaConSolicitudActiva) {
			return "redirect:/mascotas?error=mascota-ocupada";
		}

		boolean usuarioConSolicitudActiva = solicitudService.listarSolicitudesPorUsuario(usuario.getId()).stream()
				.anyMatch(s -> s.getEstado() != null && "PENDIENTE".equalsIgnoreCase(s.getEstado().getId()));
		if (usuarioConSolicitudActiva) {
			return "redirect:/adopcion/mis-adopciones?error=solicitud-activa";
		}

		EstadoAdopcion estado = new EstadoAdopcion();
		estado.setId("PENDIENTE");
		solicitud.setEstado(estado);
		solicitud.setUsuario(usuario);
		solicitud.setMascota(mascota);

		try {
			solicitudService.createSolicitud(solicitud);
		} catch (DataIntegrityViolationException ex) {
			return "redirect:/mascotas?duplicate=true";
		}
		return "redirect:/adopcion/mis-adopciones?created";
	}

	@PreAuthorize("hasRole('GESTOR')")
	@GetMapping("/gestor/solicitudes")
	public String verSolicitudesPendientes(Model model) {
		model.addAttribute("solicitudes", solicitudService.listarSolicitudesPendientes());
		model.addAttribute("mascota", null);
		model.addAttribute("vistaGlobal", true);
		return "adopcion/solicitudes";
	}

	@PreAuthorize("hasRole('GESTOR')")
	@GetMapping("/gestor/solicitudes/{mascotaId}")
	public String verSolicitudesPorMascota(@PathVariable Integer mascotaId, Model model) {
		model.addAttribute("solicitudes", solicitudService.listarSolicitudesPorMascota(mascotaId));
		model.addAttribute("mascota", mascotaService.findMascotaById(mascotaId).orElse(null));
		model.addAttribute("vistaGlobal", false);
		return "adopcion/solicitudes";
	}

	@PreAuthorize("hasRole('GESTOR')")
	@PostMapping("/gestor/aprobar/{solicitudId}")
	public String aprobarSolicitud(@PathVariable Integer solicitudId, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Integer gestorId = principal.getUsuario().getId();
		adopcionService.aprobarSolicitud(solicitudId, gestorId);
		Integer mascotaId = solicitudService.findSolicitudById(solicitudId).map(s -> s.getMascota().getId()).orElse(null);
		if (mascotaId == null) {
			return "redirect:/gestor/mascotas?aprobada";
		}
		return "redirect:/adopcion/gestor/solicitudes/" + mascotaId + "?aprobada";
	}

	@PreAuthorize("hasRole('GESTOR')")
	@PostMapping("/gestor/rechazar/{solicitudId}")
	public String rechazarSolicitud(@PathVariable Integer solicitudId,
			@RequestParam(defaultValue = "No cumple criterios de adopción") String motivo,
			Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		solicitudService.rechazarSolicitud(solicitudId, principal.getUsuario().getId(), motivo);
		Integer mascotaId = solicitudService.findSolicitudById(solicitudId).map(s -> s.getMascota().getId()).orElse(null);
		if (mascotaId == null) {
			return "redirect:/adopcion/gestor/solicitudes?rechazada";
		}
		return "redirect:/adopcion/gestor/solicitudes/" + mascotaId + "?rechazada";
	}

	@PreAuthorize("hasRole('ADOPTANTE')")
	@PostMapping("/cancelar/{solicitudId}")
	public String cancelarSolicitud(@PathVariable Integer solicitudId, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		solicitudService.cancelarSolicitud(solicitudId, principal.getUsuario().getId());
		return "redirect:/adopcion/mis-adopciones?cancelada";
	}

	@PreAuthorize("hasRole('ADOPTANTE')")
	@GetMapping("/mis-adopciones")
	public String misAdopciones(Model model, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Integer usuarioId = principal.getUsuario().getId();
		model.addAttribute("adopciones", adopcionService.listarAdopcionesPorUsuario(usuarioId));
		model.addAttribute("solicitudes", solicitudService.listarSolicitudesPorUsuario(usuarioId));
		return "adopcion/mis-adopciones";
	}
}
