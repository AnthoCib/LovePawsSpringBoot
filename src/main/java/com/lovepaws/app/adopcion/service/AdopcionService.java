package com.lovepaws.app.adopcion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lovepaws.app.adopcion.domain.Adopcion;

@Service
public interface AdopcionService {

	Adopcion createAdopcion(Adopcion adopcion);

	Optional<Adopcion> findAdopcionById(Integer id);

	List<Adopcion> listarAdopcionesPorUsuario(Integer usuarioId);

	List<Adopcion> listarAdopciones();

	Adopcion aprobarSolicitud(Integer solicitudId, Integer gestorId);
	
	void validarLimiteAnual(Integer adoptanteId);
	
	 void validarReglaSeisMesesYSeguimientos(Integer adoptanteId);

}
