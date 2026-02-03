package com.lovepaws.app.adopcion.service;

import java.util.List;
import java.util.Optional;

import com.lovepaws.app.adopcion.domain.Adopcion;

public interface AdopcionService {

	Adopcion createAdopcion(Adopcion adopcion);

	Optional<Adopcion> findAdopcionById(Integer id);

	List<Adopcion> listarAdopcionesPorUsuario(Integer usuarioId);

	List<Adopcion> listarAdopciones();

	// flujo crítico
	/**
	 * Aprueba una solicitud de adopción.
	 * Flujo transaccional:
	 * Bloquea solicitud
	 * Bloquea mascota
	 * Verifica estados
	 * Crea adopción
	 * Actualiza estados
	 */
	Adopcion aprobarSolicitud(Integer solicitudId, Integer gestorId);
}
