package com.lovepaws.app.adopcion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.adopcion.service.SolicitudAdopcionService;
import com.lovepaws.app.mail.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

	private final SolicitudAdopcionRepository solicitudRepo;
	private final EmailService emailService;
	private static final Logger logger = LoggerFactory.getLogger(SolicitudAdopcionServiceImpl.class);

	private static final String ESTADO_PENDIENTE = "PENDIENTE";
	private static final String ESTADO_APROBADA = "APROBADA";
	private static final String ESTADO_RECHAZADA = "RECHAZADA";

	@Override
	@Transactional
	public SolicitudAdopcion createSolicitud(SolicitudAdopcion solicitud) {
		// TODO Auto-generated method stub
		return solicitudRepo.save(solicitud);
	}

	@Override
	public List<SolicitudAdopcion> listarSolicitudesPorMascota(Integer mascotaId) {
		// TODO Auto-generated method stub
		return solicitudRepo.findByMascota_IdAndEstado_Id(mascotaId, ESTADO_PENDIENTE);
	}

	@Override
	public List<SolicitudAdopcion> listarSolicitudesPorUsuario(Integer usuarioId) {
		// TODO Auto-generated method stub
		return solicitudRepo.findByUsuario_Id(usuarioId);
	}

	@Override
	public List<SolicitudAdopcion> listarSolicitudesPendientes() {
		return solicitudRepo.findByEstado_Id(ESTADO_PENDIENTE);
	}

	@Override
	public Optional<SolicitudAdopcion> findSolicitudById(Integer id) {
		// TODO Auto-generated method stub
		return solicitudRepo.findById(id);
	}

	@Override
	@Transactional
	public SolicitudAdopcion updateSolicitud(SolicitudAdopcion solicitud) {
		// TODO Auto-generated method stub
		return solicitudRepo.save(solicitud);
	}

	@Override
	@Transactional
	public void deleteSolicitudById(Integer id) {
		// TODO Auto-generated method stub
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

		if (!"PENDIENTE".equalsIgnoreCase(solicitud.getEstado().getId())) {
			throw new IllegalStateException("Solicitud no está pendiente");
		}

		// Cambiar estado a APROBADA
		EstadoAdopcion estado = new EstadoAdopcion();
		estado.setId(ESTADO_APROBADA);
		solicitud.setEstado(estado);
		solicitud.setInfoAdicional("Aprobada por gestor ID: " + gestorId);

		SolicitudAdopcion solicitudActualizada = solicitudRepo.save(solicitud);

		// Enviar correo de forma asíncrona
		enviarCorreoAprobacion(solicitudActualizada);

		return solicitudActualizada;
	}

	@Async
	private void enviarCorreoAprobacion(SolicitudAdopcion solicitud) {
		try {
			String emailUsuario = solicitud.getUsuario().getCorreo();
			String asunto = "Tu solicitud de adopción ha sido aprobada";
			String contenido = "Hola " + solicitud.getUsuario().getNombre() + ",\n\n"
					+ "Tu solicitud para adoptar la mascota " + solicitud.getMascota().getNombre()
					+ " ha sido aprobada. ¡Gracias por usar Lovepaws!\n\nSaludos,\nEquipo Lovepaws";

			emailService.enviarCorreo(emailUsuario, asunto, contenido);
		} catch (Exception e) {
			logger.error("Error enviando correo de aprobación", e);
		}
	}

	@Override
	@Transactional
	public SolicitudAdopcion rechazarSolicitud(Integer solicitudId, Integer gestorId, String motivo) {

		SolicitudAdopcion solicitud = solicitudRepo.findById(solicitudId)
				.orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

		if (!"PENDIENTE".equalsIgnoreCase(solicitud.getEstado().getId())) {
			throw new IllegalStateException("Solicitud no está pendiente");
		}

		EstadoAdopcion estadoAdopcion = new EstadoAdopcion();
		estadoAdopcion.setId("RECHAZADA");
		solicitud.setEstado(estadoAdopcion);

		// opcional: guardar motivo en observaciones
		solicitud.setInfoAdicional(motivo);

		SolicitudAdopcion solicitudActualizada = solicitudRepo.save(solicitud);

	       // Enviar correo de forma asíncrona
        enviarCorreoRechazo(solicitudActualizada, motivo);

		return solicitudActualizada;
	}
	 @Async
	    private void enviarCorreoRechazo(SolicitudAdopcion solicitud, String motivo) {
	        try {
	            String emailUsuario = solicitud.getUsuario().getCorreo();
	            String asunto = "Tu solicitud de adopción ha sido rechazada";
	            String contenido = "Hola " + solicitud.getUsuario().getNombre() + ",\n\n"
	                    + "Lamentamos informarte que tu solicitud para adoptar la mascota "
	                    + solicitud.getMascota().getNombre() + " ha sido rechazada.\n"
	                    + "Motivo: " + motivo + "\n\nGracias por usar Lovepaws.\nSaludos,\nEquipo Lovepaws";

	            emailService.enviarCorreo(emailUsuario, asunto, contenido);
	        } catch (Exception e) {
	            logger.error("Error enviando correo de rechazo", e);
	        }
	    }

}
