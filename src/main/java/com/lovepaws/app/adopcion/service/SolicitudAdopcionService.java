package com.lovepaws.app.adopcion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;

@Service
public interface SolicitudAdopcionService {

	SolicitudAdopcion createSolicitud(SolicitudAdopcion solicitud);

	List<SolicitudAdopcion> listarSolicitudesPorMascota(Integer mascotaId);

	List<SolicitudAdopcion> listarSolicitudesPorUsuario(Integer usuarioId);

	List<SolicitudAdopcion> listarSolicitudesPendientes();

	List<SolicitudAdopcion> listarSolicitudesGestor();

	Optional<SolicitudAdopcion> findSolicitudById(Integer id);

	SolicitudAdopcion updateSolicitud(SolicitudAdopcion solicitud);

	void deleteSolicitudById(Integer id);

	// métodos para gestor:
	SolicitudAdopcion aprobarSolicitud(Integer solicitudId, Integer gestorId);

	SolicitudAdopcion rechazarSolicitud(Integer solicitudId, Integer gestorId, String motivo);

	SolicitudAdopcion cancelarSolicitud(Integer solicitudId, Integer usuarioId);

	SolicitudAdopcion decidirSolicitud(Integer solicitudId, Integer gestorId, String accion, String motivo);

}
