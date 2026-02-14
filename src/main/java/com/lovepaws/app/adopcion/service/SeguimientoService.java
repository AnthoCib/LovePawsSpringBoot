package com.lovepaws.app.adopcion.service;

import java.util.List;
import java.util.Optional;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;

public interface SeguimientoService {

	SeguimientoAdopcion createSeguimiento(SeguimientoAdopcion seguimiento, Integer usuarioId, String usuarioNombre);

	List<SeguimientoAdopcion> listarPorAdopcion(Integer adopcionId);

	Optional<SeguimientoAdopcion> findById(Integer id);

	void eliminarLogico(Integer seguimientoId, Integer usuarioId, String usuarioNombre);

}
