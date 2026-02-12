package com.lovepaws.app.adopcion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.adopcion.service.NotificacionEmailService;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

	private final SolicitudAdopcionRepository solicitudRepo;
	private final AdopcionRepository adopcionRepository;
	private final NotificacionEmailService notificacionEmailService;
	private final MascotaRepository mascotaRepository;
	private final AuditoriaService auditoriaService;
	private static final String ESTADO_PENDIENTE = "PENDIENTE";
	private static final String ESTADO_APROBADA = "APROBADA";
	private static final String ESTADO_RECHAZADA = "RECHAZADA";
	private static final String ESTADO_CANCELADA = "CANCELADA";

	@Override
	@Transactional
	public SolicitudAdopcion createSolicitud(SolicitudAdopcion solicitud) {
		if (solicitud.getUsuario() == null || solicitud.getMascota() == null) {
			throw new IllegalArgumentException("Solicitud incompleta");
		}

		Mascota mascota = mascotaRepository.findById(solicitud.getMascota().getId())
				.orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
		if (mascota.getEstado() == null || !"DISPONIBLE".equalsIgnoreCase(mascota.getEstado().getId())) {
			throw new IllegalStateException("La mascota no se encuentra disponible para adopción");
		}

		if (solicitudRepo.existsByMascota_IdAndEstado_Id(mascota.getId(), ESTADO_PENDIENTE)) {
			throw new IllegalStateException("La mascota ya tiene una solicitud activa");
		}

		Integer usuarioId = solicitud.getUsuario().getId();
		if (solicitudRepo.existsByUsuario_IdAndEstado_Id(usuarioId, ESTADO_PENDIENTE)) {
			throw new IllegalStateException("El usuario ya tiene una solicitud activa");
		}

		if (solicitud.getUsuario().getNombre() == null || solicitud.getUsuario().getNombre().isBlank()
				|| solicitud.getUsuario().getCorreo() == null || !solicitud.getUsuario().getCorreo().contains("@")
				|| solicitud.getUsuario().getTelefono() == null || solicitud.getUsuario().getTelefono().isBlank()
				|| solicitud.getUsuario().getDireccion() == null || solicitud.getUsuario().getDireccion().isBlank()) {
			throw new IllegalStateException("Datos personales o medio de contacto incompletos");
		}

		SolicitudAdopcion saved = solicitudRepo.save(solicitud);
		String usuarioNombre = saved.getUsuario() != null ? saved.getUsuario().getNombre() : "Sistema";
		auditoriaService.registrar("solicitud_adopcion", saved.getId(), "CREAR_SOLICITUD", usuarioId, usuarioNombre,
				"Solicitud creada para mascota " + (saved.getMascota() != null ? saved.getMascota().getId() : "-"));
		DatosCorreoSolicitud datosCorreo = extraerDatosCorreo(saved);
		notificacionEmailService.enviarCorreoRecepcion(datosCorreo.getCorreoDestino(), datosCorreo.getNombreUsuario(), datosCorreo.getNombreMascota());
		return saved;
	}

	@Override
	public List<SolicitudAdopcion> listarSolicitudesPorMascota(Integer mascotaId) {
		return solicitudRepo.findByMascota_IdOrderByFechaSolicitudDesc(mascotaId);
	}

	@Override
	public List<SolicitudAdopcion> listarSolicitudesPorUsuario(Integer usuarioId) {
		return solicitudRepo.findByUsuario_Id(usuarioId);
	}

	@Override
	public List<SolicitudAdopcion> listarSolicitudesPendientes() {
		return solicitudRepo.findByEstado_Id(ESTADO_PENDIENTE);
	}

	@Override
	public List<SolicitudAdopcion> listarSolicitudesGestor() {
		return solicitudRepo.findAllByOrderByFechaSolicitudDesc();
	}

	@Override
	public Optional<SolicitudAdopcion> findSolicitudById(Integer id) {
		return solicitudRepo.findById(id);
	}

	@Override
	@Transactional
	public SolicitudAdopcion updateSolicitud(SolicitudAdopcion solicitud) {
		return solicitudRepo.save(solicitud);
	}

	@Override
	@Transactional
	public void deleteSolicitudById(Integer id) {
		solicitudRepo.findById(id).ifPresent(s -> {
			s.setDeletedAt(LocalDateTime.now());
			solicitudRepo.save(s);
		});
	}

	@Override
	@Transactional
	public SolicitudAdopcion aprobarSolicitud(Integer solicitudId, Integer gestorId) {
		return decidirSolicitud(solicitudId, gestorId, "APROBAR", null);
	}

	@Override
	@Transactional
	public SolicitudAdopcion rechazarSolicitud(Integer solicitudId, Integer gestorId, String motivo) {
		return decidirSolicitud(solicitudId, gestorId, "RECHAZAR", motivo);
	}

	@Override
	@Transactional
	public SolicitudAdopcion cancelarSolicitud(Integer solicitudId, Integer usuarioId) {
		SolicitudAdopcion solicitud = solicitudRepo.findById(solicitudId)
				.orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

		if (!solicitud.getUsuario().getId().equals(usuarioId)) {
			throw new IllegalStateException("No puedes cancelar solicitudes de otro usuario");
		}
		if (!ESTADO_PENDIENTE.equalsIgnoreCase(solicitud.getEstado().getId())) {
			throw new IllegalStateException("Solo se puede cancelar solicitudes pendientes");
		}

		EstadoAdopcion estado = new EstadoAdopcion();
		estado.setId(ESTADO_CANCELADA);
		solicitud.setEstado(estado);
		solicitud.setInfoAdicional("Solicitud cancelada por usuario");
		SolicitudAdopcion updated = solicitudRepo.save(solicitud);

		Usuario u = updated.getUsuario();
		auditoriaService.registrar("solicitud_adopcion", updated.getId(), "UPDATE", usuarioId,
				u != null ? u.getNombre() : "USUARIO", "Estado cambiado a CANCELADA");
		DatosCorreoSolicitud datosCorreo = extraerDatosCorreo(updated);
		notificacionEmailService.enviarCorreoCancelacion(datosCorreo.getCorreoDestino(), datosCorreo.getNombreUsuario(), datosCorreo.getNombreMascota());
		return updated;
	}

	@Override
	@Transactional
	public SolicitudAdopcion decidirSolicitud(Integer solicitudId, Integer gestorId, String accion, String motivo) {
		if (accion == null || accion.isBlank()) {
			throw new IllegalArgumentException("Acción inválida");
		}

		String accionNormalizada = accion.trim().toUpperCase();
		if (!"APROBAR".equals(accionNormalizada) && !"RECHAZAR".equals(accionNormalizada)) {
			throw new IllegalArgumentException("Acción inválida");
		}

		SolicitudAdopcion solicitud = solicitudRepo.findByIdForUpdate(solicitudId)
				.orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

		if (solicitud.getEstado() == null || solicitud.getEstado().getId() == null
				|| !ESTADO_PENDIENTE.equalsIgnoreCase(solicitud.getEstado().getId())) {
			throw new IllegalStateException("La solicitud ya no está pendiente");
		}

		if ("APROBAR".equals(accionNormalizada)) {
			aprobarConAdopcion(solicitud, gestorId);
		} else {
			rechazarConMotivo(solicitud, gestorId, motivo);
		}

		return solicitudRepo.save(solicitud);
	}

	private void aprobarConAdopcion(SolicitudAdopcion solicitud, Integer gestorId) {
		if (solicitud.getMascota() == null || solicitud.getMascota().getId() == null) {
			throw new IllegalStateException("Solicitud sin mascota asociada");
		}

		Mascota mascota = mascotaRepository.findByIdForUpdate(solicitud.getMascota().getId())
				.orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));

		if (mascota.getEstado() == null || mascota.getEstado().getId() == null
				|| !"DISPONIBLE".equalsIgnoreCase(mascota.getEstado().getId())) {
			throw new IllegalStateException("La mascota no está disponible para aprobar esta solicitud");
		}

		if (adopcionRepository.existsBySolicitud_Id(solicitud.getId())) {
			throw new IllegalStateException("La solicitud ya tiene una adopción registrada");
		}

		EstadoAdopcion estadoAprobada = new EstadoAdopcion();
		estadoAprobada.setId(ESTADO_APROBADA);
		solicitud.setEstado(estadoAprobada);
		solicitud.setInfoAdicional("Aprobada por gestor ID: " + gestorId);

		Usuario gestor = new Usuario();
		gestor.setId(gestorId);

		Adopcion adopcion = Adopcion.builder()
				.usuarioAdoptante(solicitud.getUsuario())
				.mascota(mascota)
				.estado(estadoAprobada)
				.solicitud(solicitud)
				.fechaAdopcion(LocalDateTime.now())
				.activo(true)
				.usuarioCreacion(gestor)
				.build();
		adopcionRepository.save(adopcion);

		EstadoMascota estadoAdoptada = new EstadoMascota();
		estadoAdoptada.setId("ADOPTADA");
		mascota.setEstado(estadoAdoptada);
		mascotaRepository.save(mascota);

		auditoriaService.registrar("solicitud_adopcion", solicitud.getId(), "UPDATE", gestorId,
				"GESTOR", "Solicitud APROBADA, mascota ADOPTADA y adopción creada");

		DatosCorreoSolicitud datosCorreo = extraerDatosCorreo(solicitud);
		notificacionEmailService.enviarCorreoAprobacion(datosCorreo.getCorreoDestino(), datosCorreo.getNombreUsuario(), datosCorreo.getNombreMascota());
	}

	private void rechazarConMotivo(SolicitudAdopcion solicitud, Integer gestorId, String motivo) {
		EstadoAdopcion estadoRechazada = new EstadoAdopcion();
		estadoRechazada.setId(ESTADO_RECHAZADA);
		solicitud.setEstado(estadoRechazada);
		String motivoFinal = (motivo == null || motivo.isBlank()) ? "No cumple criterios de adopción" : motivo.trim();
		solicitud.setInfoAdicional(motivoFinal);

		auditoriaService.registrar("solicitud_adopcion", solicitud.getId(), "UPDATE", gestorId,
				"GESTOR", "Solicitud RECHAZADA. Motivo: " + motivoFinal);

		DatosCorreoSolicitud datosCorreo = extraerDatosCorreo(solicitud);
		notificacionEmailService.enviarCorreoRechazo(datosCorreo.getCorreoDestino(), datosCorreo.getNombreUsuario(), datosCorreo.getNombreMascota(), motivoFinal);
	}

	private DatosCorreoSolicitud extraerDatosCorreo(SolicitudAdopcion solicitud) {
		if (solicitud == null || solicitud.getUsuario() == null || solicitud.getMascota() == null) {
			return new DatosCorreoSolicitud(null, null, null);
		}
		return new DatosCorreoSolicitud(
				solicitud.getUsuario().getCorreo(),
				solicitud.getUsuario().getNombre(),
				solicitud.getMascota().getNombre()
		);
	}

	@Getter
	@AllArgsConstructor
	private static class DatosCorreoSolicitud {
		private final String correoDestino;
		private final String nombreUsuario;
		private final String nombreMascota;
	}

}
