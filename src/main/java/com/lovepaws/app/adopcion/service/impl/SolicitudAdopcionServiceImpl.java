package com.lovepaws.app.adopcion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.adopcion.service.NotificacionEmailService;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

	private final SolicitudAdopcionRepository solicitudRepo;
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
		notificacionEmailService.notificarRecepcionSolicitud(saved);
		return saved;
	}

	@Override
	public List<SolicitudAdopcion> listarSolicitudesPorMascota(Integer mascotaId) {
		return solicitudRepo.findByMascota_IdAndEstado_Id(mascotaId, ESTADO_PENDIENTE);
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
		SolicitudAdopcion solicitud = solicitudRepo.findById(solicitudId)
				.orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

		if (!ESTADO_PENDIENTE.equalsIgnoreCase(solicitud.getEstado().getId())) {
			throw new IllegalStateException("Solicitud no está pendiente");
		}

		EstadoAdopcion estado = new EstadoAdopcion();
		estado.setId(ESTADO_APROBADA);
		solicitud.setEstado(estado);
		solicitud.setInfoAdicional("Aprobada por gestor ID: " + gestorId);

		SolicitudAdopcion solicitudActualizada = solicitudRepo.save(solicitud);
		auditoriaService.registrar("solicitud_adopcion", solicitudActualizada.getId(), "UPDATE", gestorId,
				"GESTOR", "Estado cambiado a APROBADA");
		notificacionEmailService.notificarSolicitudAprobada(solicitudActualizada);

		return solicitudActualizada;
	}

	@Override
	@Transactional
	public SolicitudAdopcion rechazarSolicitud(Integer solicitudId, Integer gestorId, String motivo) {

		SolicitudAdopcion solicitud = solicitudRepo.findById(solicitudId)
				.orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

		if (!ESTADO_PENDIENTE.equalsIgnoreCase(solicitud.getEstado().getId())) {
			throw new IllegalStateException("Solicitud no está pendiente");
		}

		EstadoAdopcion estadoAdopcion = new EstadoAdopcion();
		estadoAdopcion.setId(ESTADO_RECHAZADA);
		solicitud.setEstado(estadoAdopcion);
		solicitud.setInfoAdicional(motivo);

		SolicitudAdopcion solicitudActualizada = solicitudRepo.save(solicitud);
		auditoriaService.registrar("solicitud_adopcion", solicitudActualizada.getId(), "UPDATE", gestorId,
				"GESTOR", "Estado cambiado a RECHAZADA. Motivo: " + motivo);
		notificacionEmailService.notificarSolicitudRechazada(solicitudActualizada, motivo);

		return solicitudActualizada;
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
		notificacionEmailService.notificarSolicitudCancelada(updated);
		return updated;
	}

}
