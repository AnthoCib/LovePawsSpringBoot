package com.lovepaws.app.adopcion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.adopcion.service.AdopcionService;
import com.lovepaws.app.adopcion.service.NotificacionEmailService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.AuditoriaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdopcionServiceImpl implements AdopcionService {

	private final AdopcionRepository adopcionRepo;
	private final SolicitudAdopcionRepository solicitudRepo;
	private final MascotaRepository mascotaRepo;
	private final NotificacionEmailService notificacionEmailService;
	private final AuditoriaService auditoriaService;

	@Override
	public Adopcion createAdopcion(Adopcion adopcion) {
		if (adopcion.getFechaAdopcion() == null)
			adopcion.setFechaAdopcion(LocalDateTime.now());
		return adopcionRepo.save(adopcion);
	}

	@Override
	public Optional<Adopcion> findAdopcionById(Integer id) {
		return adopcionRepo.findById(id);
	}

	@Override
	public List<Adopcion> listarAdopcionesPorUsuario(Integer usuarioId) {
		return adopcionRepo.findByUsuarioAdoptante_Id(usuarioId);
	}

	@Override
	public List<Adopcion> listarAdopciones() {
		return adopcionRepo.findAll();
	}

	@Override
	@Transactional
	public Adopcion aprobarSolicitud(Integer solicitudId, Integer gestorId) {

		SolicitudAdopcion solicitud = solicitudRepo.findByIdForUpdate(solicitudId)
				.orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

		if (solicitud.getEstado() == null || solicitud.getEstado().getId() == null) {
			throw new IllegalStateException("Solicitud sin estado válido");
		}

		if (!"PENDIENTE".equals(solicitud.getEstado().getId())) {
			throw new IllegalStateException("Solicitud no está pendiente");
		}

		if (solicitud.getMascota() == null || solicitud.getMascota().getId() == null) {
			throw new IllegalStateException("Solicitud sin mascota asociada");
		}

		if (solicitud.getUsuario() == null || solicitud.getUsuario().getId() == null) {
			throw new IllegalStateException("Solicitud sin adoptante asociado");
		}

		if (adopcionRepo.existsBySolicitud_Id(solicitud.getId())) {
			throw new IllegalStateException("La solicitud ya tiene una adopción registrada");
		}

		Mascota mascota = mascotaRepo.findByIdForUpdate(solicitud.getMascota().getId())
				.orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));

		if (mascota.getEstado() == null || mascota.getEstado().getId() == null) {
			throw new IllegalStateException("Mascota sin estado válido");
		}

		if (!"DISPONIBLE".equals(mascota.getEstado().getId())) {
			throw new IllegalStateException("Mascota no disponible");
		}

		if (!adopcionRepo.findByMascotaId(mascota.getId()).isEmpty()) {
			throw new IllegalStateException("La mascota ya cuenta con una adopción registrada");
		}

		EstadoAdopcion estadoAdopcion = new EstadoAdopcion();
		estadoAdopcion.setId("APROBADA");

		Usuario gestor = new Usuario();
		gestor.setId(gestorId);

		Adopcion adopcion = Adopcion.builder()
				.usuarioAdoptante(solicitud.getUsuario())
				.mascota(mascota)
				.estado(estadoAdopcion)
				.solicitud(solicitud)
				.fechaAdopcion(LocalDateTime.now())
				.activo(true)
				.usuarioCreacion(gestor)
				.build();

		Adopcion saved = adopcionRepo.save(adopcion);

		solicitud.setEstado(estadoAdopcion);
		solicitud.setInfoAdicional("Aprobada por gestor ID: " + gestorId);
		solicitudRepo.save(solicitud);

		EstadoMascota estadoMascota = new EstadoMascota();
		estadoMascota.setId("ADOPTADA");
		mascota.setEstado(estadoMascota);
		mascotaRepo.save(mascota);

		auditoriaService.registrar("solicitud_adopcion", solicitud.getId(), "UPDATE", gestorId, "GESTOR",
				"Solicitud cambiada a APROBADA y mascota cambiada a ADOPTADA");
		auditoriaService.registrar("adopcion", saved.getId(), "UPDATE", gestorId, "GESTOR",
				"Estado de adopción establecido en APROBADA");

		notificacionEmailService.notificarSolicitudAprobada(solicitud);
		return saved;
	}

}
