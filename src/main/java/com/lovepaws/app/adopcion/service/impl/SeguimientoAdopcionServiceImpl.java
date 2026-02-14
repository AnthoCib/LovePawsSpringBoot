package com.lovepaws.app.adopcion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;
import com.lovepaws.app.adopcion.service.SeguimientoService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.EstadoMascotaRepository;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoAdopcionServiceImpl implements SeguimientoService {

	private final SeguimientoAdopcionRepository seguimientoRepo;
	private final AdopcionRepository adopcionRepository;
	private final MascotaRepository mascotaRepository;
	private final EstadoMascotaRepository estadoMascotaRepository;
	private final AuditoriaService auditoriaService;

	@Override
	@Transactional
	public SeguimientoAdopcion createSeguimiento(SeguimientoAdopcion seguimiento, Integer usuarioId, String usuarioNombre) {
		if (seguimiento.getAdopcion() == null || seguimiento.getAdopcion().getId() == null) {
			throw new IllegalArgumentException("La adopción es obligatoria para registrar seguimiento");
		}

		Adopcion adopcion = adopcionRepository
				.findByIdAndDeletedAtIsNullAndActivoTrue(seguimiento.getAdopcion().getId())
				.orElseThrow(() -> new IllegalArgumentException("La adopción no existe, fue eliminada o está inactiva"));

		if (adopcion.getEstado() == null || !"APROBADA".equalsIgnoreCase(adopcion.getEstado().getId())) {
			throw new IllegalStateException("Solo se puede crear seguimiento para adopciones APROBADAS");
		}

		if (seguimiento.getFechaVisita() == null) {
			throw new IllegalArgumentException("La fecha de visita es obligatoria");
		}

		LocalDateTime fechaAdopcion = adopcion.getFechaAdopcion();
		if (fechaAdopcion != null && seguimiento.getFechaVisita().isBefore(fechaAdopcion)) {
			throw new IllegalStateException("La fecha de visita no puede ser anterior a la fecha de adopción");
		}

		seguimiento.setAdopcion(adopcion);

		if (seguimiento.getEstado() != null && seguimiento.getEstado().getId() != null && !seguimiento.getEstado().getId().isBlank()) {
			EstadoMascota estadoMascota = estadoMascotaRepository.findById(seguimiento.getEstado().getId())
					.orElseThrow(() -> new IllegalArgumentException("Estado de mascota no válido"));
			seguimiento.setEstado(estadoMascota);
		}
		if (seguimiento.getActivo() == null) {
			seguimiento.setActivo(Boolean.TRUE);
		}

		SeguimientoAdopcion saved = seguimientoRepo.save(seguimiento);
		auditoriaService.registrar("seguimiento_post_adopcion", saved.getId(), "INSERT", usuarioId,
				usuarioNombre, "Seguimiento post adopción registrado");

		if (saved.getEstado() != null && adopcion.getMascota() != null && adopcion.getMascota().getId() != null) {
			Mascota mascota = mascotaRepository.findById(adopcion.getMascota().getId())
					.orElseThrow(() -> new IllegalStateException("Mascota asociada no encontrada"));
			mascota.setEstado(saved.getEstado());
			mascotaRepository.save(mascota);
			auditoriaService.registrar("mascota", mascota.getId(), "UPDATE", usuarioId,
					usuarioNombre, "Actualización de estado de mascota desde seguimiento post adopción");
		}

		return saved;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SeguimientoAdopcion> listarPorAdopcion(Integer adopcionId) {
		return seguimientoRepo.findByAdopcionIdOrderByFechaVisitaDesc(adopcionId);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SeguimientoAdopcion> findById(Integer id) {
		return seguimientoRepo.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	@Transactional
	public void eliminarLogico(Integer seguimientoId, Integer usuarioId, String usuarioNombre) {
		SeguimientoAdopcion seguimiento = seguimientoRepo.findByIdAndDeletedAtIsNull(seguimientoId)
				.orElseThrow(() -> new IllegalArgumentException("Seguimiento no encontrado"));
		seguimientoRepo.delete(seguimiento);
		auditoriaService.registrar("seguimiento_post_adopcion", seguimientoId, "DELETE", usuarioId,
				usuarioNombre, "Eliminación lógica de seguimiento post adopción");
	}
}
