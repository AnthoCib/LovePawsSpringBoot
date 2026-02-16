package com.lovepaws.app.adopcion.service;

import java.util.List;
import java.util.Optional;

import com.lovepaws.app.adopcion.domain.Adopcion;

public interface AdopcionService {

	Adopcion createAdopcion(Adopcion adopcion);

	Optional<Adopcion> findAdopcionById(Integer id);

	List<Adopcion> listarAdopcionesPorUsuario(Integer usuarioId);

	List<Adopcion> listarAdopciones();

	Adopcion aprobarSolicitud(Integer solicitudId, Integer gestorId);
}
