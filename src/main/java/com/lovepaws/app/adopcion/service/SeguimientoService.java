package com.lovepaws.app.adopcion.service;

import java.util.List;
import java.util.Optional;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;

public interface SeguimientoService {

	SeguimientoPostAdopcion createSeguimiento(SeguimientoPostAdopcion seguimiento, Integer usuarioId, String usuarioNombre);

	List<SeguimientoPostAdopcion> listarPorAdopcion(Integer adopcionId);

	Optional<SeguimientoPostAdopcion> findById(Integer id);

}
