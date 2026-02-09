package com.lovepaws.app.adopcion.service;

import java.util.List;
import java.util.Optional;

import com.lovepaws.app.adopcion.domain.RespuestaSeguimientoAdoptante;

public interface RespuestaSeguimientoAdoptanteService {
	RespuestaSeguimientoAdoptante crearRespuesta(RespuestaSeguimientoAdoptante respuesta, Integer usuarioId,
			String usuarioNombre);

	List<RespuestaSeguimientoAdoptante> listarPorAdopcion(Integer adopcionId);

	List<RespuestaSeguimientoAdoptante> listarPorSeguimiento(Integer seguimientoId);

	Optional<RespuestaSeguimientoAdoptante> buscarUltimaRespuesta(Integer seguimientoId, Integer usuarioId);
}
