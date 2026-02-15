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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.SeguimientoService;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.seguimiento.repository.EstadoSeguimientoRepository;
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
	private final SeguimientoService seguimientoService;
	private final EstadoSeguimientoRepository estadoSeguimientoRepository;

	@GetMapping
	public String flujoAdopcion() {
		return "adopcion/flujo";
	}

	@PreAuthorize("hasRole('ADOPTANTE')")
	@GetMapping("/solicitud/{mascotaId}")
	public String formularioSolicitud(@PathVariable Integer mascotaId, Model model, Authentication auth) {
		Mascota mascota = mascotaService.findMascotaById(mascotaId).orElse(null);
		if (mascota == null || mascota.getEstado() == null || !"DISPONIBLE".equalsIgnoreCase(mascota.getEstado().getId())) {
			return "redirect:/mascotas?error=estado-mascota";
		}
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Usuario usuario = principal.getUsuario();
		SolicitudAdopcion solicitud = new SolicitudAdopcion();
		solicitud.setMascota(mascota);
		model.addAttribute("solicitud", solicitud);
		model.addAttribute("mascota", mascota);
		model.addAttribute("usuario", usuario);
		return "adopcion/solicitud-form";
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

		if (camposSolicitudIncompletos(solicitud)) {
			return "redirect:/mascotas/" + mascota.getId() + "?error=form";
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

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@GetMapping("/gestor/solicitudes")
	public String verSolicitudesPendientes(Model model) {
		model.addAttribute("solicitudes", solicitudService.listarSolicitudesGestor());
		model.addAttribute("mascota", null);
		model.addAttribute("vistaGlobal", true);
		return "adopcion/solicitudes";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@GetMapping("/gestor/solicitudes/{mascotaId}")
	public String verSolicitudesPorMascota(@PathVariable Integer mascotaId, Model model) {
		model.addAttribute("solicitudes", solicitudService.listarSolicitudesPorMascota(mascotaId));
		model.addAttribute("mascota", mascotaService.findMascotaById(mascotaId).orElse(null));
		model.addAttribute("vistaGlobal", false);
		return "adopcion/solicitudes";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@GetMapping("/gestor/adopciones")
	public String verAdopcionesGestor(Model model) {
		model.addAttribute("adopciones", adopcionService.listarAdopciones());
		return "adopcion/adopciones-gestor";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/aprobar/{solicitudId}")
	public String aprobarSolicitud(@PathVariable Integer solicitudId, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Integer gestorId = principal.getUsuario().getId();
		solicitudService.decidirSolicitud(solicitudId, gestorId, "APROBAR", null);
		Integer mascotaId = solicitudService.findSolicitudById(solicitudId).map(s -> s.getMascota().getId()).orElse(null);
		if (mascotaId == null) {
			return "redirect:/gestor/mascotas?aprobada";
		}
		return "redirect:/adopcion/gestor/solicitudes/" + mascotaId + "?aprobada";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/rechazar/{solicitudId}")
	public String rechazarSolicitud(@PathVariable Integer solicitudId,
			@RequestParam(defaultValue = "No cumple criterios de adopción") String motivo,
			Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		solicitudService.decidirSolicitud(solicitudId, principal.getUsuario().getId(), "RECHAZAR", motivo);
		Integer mascotaId = solicitudService.findSolicitudById(solicitudId).map(s -> s.getMascota().getId()).orElse(null);
		if (mascotaId == null) {
			return "redirect:/adopcion/gestor/solicitudes?rechazada";
		}
		return "redirect:/adopcion/gestor/solicitudes/" + mascotaId + "?rechazada";
	}

	@PreAuthorize("hasAnyRole('GESTOR','ADMIN')")
	@PostMapping("/gestor/solicitudes/{solicitudId}/decision")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> decidirSolicitudAjax(@PathVariable Integer solicitudId,
			@RequestParam String accion,
			@RequestParam(required = false) String motivo,
			Authentication auth) {
		try {
			UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
			SolicitudAdopcion solicitud = solicitudService.decidirSolicitud(solicitudId, principal.getUsuario().getId(), accion, motivo);
			String estado = solicitud.getEstado() != null ? solicitud.getEstado().getId() : "-";
			return ResponseEntity.ok(Map.of(
					"ok", true,
					"solicitudId", solicitud.getId(),
					"estado", estado,
					"mensaje", "Solicitud actualizada correctamente"
			));
		} catch (IllegalArgumentException | IllegalStateException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
					"ok", false,
					"mensaje", ex.getMessage()
			));
		}
	}


	@PreAuthorize("hasRole('ADOPTANTE')")
	@GetMapping("/mis-adopciones")
	public String misAdopciones(Model model, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Integer usuarioId = principal.getUsuario().getId();
		List<com.lovepaws.app.adopcion.domain.Adopcion> adopciones = adopcionService.listarAdopcionesPorUsuario(usuarioId);
		Set<Integer> solicitudIdsConAdopcion = adopciones.stream()
				.map(com.lovepaws.app.adopcion.domain.Adopcion::getSolicitud)
				.filter(s -> s != null && s.getId() != null)
				.map(SolicitudAdopcion::getId)
				.collect(Collectors.toSet());
		List<SolicitudAdopcion> solicitudesSinAdopcion = solicitudService.listarSolicitudesPorUsuario(usuarioId).stream()
				.filter(s -> s.getId() != null && !solicitudIdsConAdopcion.contains(s.getId()))
				.collect(Collectors.toList());
		model.addAttribute("adopciones", adopciones);
		model.addAttribute("solicitudes", solicitudesSinAdopcion);
		model.addAttribute("totalPendientes", solicitudesSinAdopcion.stream()
				.filter(s -> s.getEstado() != null && "PENDIENTE".equalsIgnoreCase(s.getEstado().getId()))
				.count());
		model.addAttribute("totalAprobadas", adopciones.stream()
				.filter(a -> a.getEstado() != null && "APROBADA".equalsIgnoreCase(a.getEstado().getId()))
				.count());
		return "adopcion/mis-adopciones";
	}

	@PreAuthorize("hasRole('ADOPTANTE')")
	@PostMapping("/mis-adopciones/solicitud/{solicitudId}/cancelar")
	public String cancelarSolicitudAdoptante(@PathVariable Integer solicitudId, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		try {
			solicitudService.cancelarSolicitud(solicitudId, principal.getUsuario().getId());
			return "redirect:/adopcion/mis-adopciones?cancelada";
		} catch (IllegalArgumentException | IllegalStateException ex) {
			return "redirect:/adopcion/mis-adopciones?error=cancelar";
		}
	}

	@PreAuthorize("hasRole('ADOPTANTE')")
	@GetMapping("/mis-adopciones/solicitud/{solicitudId}")
	public String verDetalleSolicitudAdoptante(@PathVariable Integer solicitudId, Model model, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Integer usuarioId = principal.getUsuario().getId();
		SolicitudAdopcion solicitud = solicitudService.findSolicitudById(solicitudId)
				.filter(s -> s.getUsuario() != null && s.getUsuario().getId().equals(usuarioId))
				.orElse(null);
		if (solicitud == null) {
			return "redirect:/adopcion/mis-adopciones?error=forbidden";
		}
		Integer adopcionId = adopcionService.listarAdopcionesPorUsuario(usuarioId).stream()
				.filter(a -> a.getSolicitud() != null && a.getSolicitud().getId() != null
						&& a.getSolicitud().getId().equals(solicitudId))
				.map(com.lovepaws.app.adopcion.domain.Adopcion::getId)
				.findFirst()
				.orElse(null);
		model.addAttribute("solicitud", solicitud);
		model.addAttribute("adopcionId", adopcionId);
		return "adopcion/solicitud-detalle";
	}


	@PreAuthorize("hasRole('ADOPTANTE')")
	@GetMapping("/seguimiento/{adopcionId}")
	public String verSeguimientoAdoptante(@PathVariable Integer adopcionId, Model model, Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		Integer usuarioId = principal.getUsuario().getId();
		com.lovepaws.app.adopcion.domain.Adopcion adopcion = adopcionService.findAdopcionById(adopcionId)
				.filter(a -> a.getUsuarioAdoptante() != null && a.getUsuarioAdoptante().getId().equals(usuarioId))
				.orElse(null);
		if (adopcion == null) {
			return "redirect:/adopcion/mis-adopciones?error=forbidden";
		}
		model.addAttribute("adopcion", adopcion);
		model.addAttribute("seguimientos", seguimientoService.listarPorAdopcion(adopcionId));
		return "adopcion/seguimiento";
	}

	@PreAuthorize("hasRole('GESTOR')")
	@GetMapping("/gestor/seguimiento/{adopcionId}")
	public String verSeguimientoGestor(@PathVariable Integer adopcionId, Model model) {
		com.lovepaws.app.adopcion.domain.Adopcion adopcion = adopcionService.findAdopcionById(adopcionId).orElse(null);
		if (adopcion == null) {
			return "redirect:/gestor/dashboard?error=adopcion";
		}
		model.addAttribute("adopcion", adopcion);
		model.addAttribute("seguimientos", seguimientoService.listarPorAdopcion(adopcionId));
		model.addAttribute("estadosSeguimiento",
				estadoSeguimientoRepository.findByIdInOrderByDescripcionAsc(EstadoMascotaTracking.idsPorTipo("MASCOTA")));
		return "adopcion/seguimiento-gestor";
	}

	@PreAuthorize("hasRole('GESTOR')")
	@PostMapping("/gestor/seguimiento/{adopcionId}")
	public String crearSeguimiento(@PathVariable Integer adopcionId,
	                               @RequestParam String fechaVisita,
	                               @RequestParam(required = false) String observaciones,
	                               @RequestParam(required = false) String estadoId,
	                               Authentication auth) {
		UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
		com.lovepaws.app.adopcion.domain.Adopcion adopcion = adopcionService.findAdopcionById(adopcionId).orElse(null);
		if (adopcion == null) {
			return "redirect:/gestor/dashboard?error=adopcion";
		}
		LocalDateTime fecha;
		try {
			fecha = LocalDateTime.parse(fechaVisita, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
		} catch (DateTimeParseException ex) {
			return "redirect:/adopcion/gestor/seguimiento/" + adopcionId + "?error=fecha";
		}
		if (adopcion.getFechaAdopcion() != null && fecha.isBefore(adopcion.getFechaAdopcion())) {
			return "redirect:/adopcion/gestor/seguimiento/" + adopcionId + "?error=fecha-min";
		}

		SeguimientoAdopcion seguimiento = new SeguimientoAdopcion();
		seguimiento.setAdopcion(adopcion);
		seguimiento.setFechaVisita(fecha);
		seguimiento.setObservaciones(observaciones != null ? observaciones.trim() : null);
		seguimiento.setUsuarioCreacion(principal.getUsuario());
		if (estadoId != null && !estadoId.isBlank()) {
			String estadoIdNormalizado = estadoId.trim().toUpperCase();
			if (!EstadoMascotaTracking.idsPorTipo("MASCOTA").contains(estadoIdNormalizado)) {
				return "redirect:/adopcion/gestor/seguimiento/" + adopcionId + "?error=estado";
			}
			EstadoSeguimiento estadoSeguimiento = estadoSeguimientoRepository.findById(estadoIdNormalizado).orElse(null);
			seguimiento.setEstado(estadoSeguimiento);
		}
		seguimientoService.createSeguimiento(seguimiento, principal.getUsuario().getId(), principal.getUsuario().getUsername());
		return "redirect:/adopcion/gestor/seguimiento/" + adopcionId + "?created";
	}

	private boolean camposSolicitudIncompletos(SolicitudAdopcion solicitud) {
		return esTextoVacio(solicitud.getTipoVivienda())
				|| esTextoVacio(solicitud.getExperiencia())
				|| esTextoVacio(solicitud.getNinosOtraMascotas())
				|| esTextoVacio(solicitud.getTiempoDedicado())
				|| esTextoVacio(solicitud.getCubrirCostos())
				|| esTextoVacio(solicitud.getPlanMascota());
	}

	private boolean esTextoVacio(String valor) {
		return valor == null || valor.trim().isBlank();
	}
}
