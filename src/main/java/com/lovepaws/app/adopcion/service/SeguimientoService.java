package com.lovepaws.app.adopcion.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import com.lovepaws.app.adopcion.domain.RespuestaSeguimientoAdopcion;
import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;

public interface SeguimientoService {

	SeguimientoPostAdopcion createSeguimiento(SeguimientoPostAdopcion seguimiento, Integer usuarioId, String usuarioNombre);

	SeguimientoPostAdopcion crearSeguimientoCompleto(Integer adopcionId,
	                                           LocalDateTime fechaVisita,
	                                           String observaciones,
	                                           String estadoMascotaId,
	                                           Integer usuarioId,
	                                           String usuarioNombre);

	RespuestaSeguimientoAdopcion responderSeguimiento(Integer seguimientoId,
	                                                  Integer adopcionId,
	                                                  String estadoSalud,
	                                                  String comportamiento,
	                                                  String alimentacion,
	                                                  String comentarios,
	                                                  Integer usuarioId,
	                                                  String usuarioNombre);

	void eliminarSeguimientoSoft(Integer seguimientoId, Integer usuarioId, String usuarioNombre);

	List<SeguimientoPostAdopcion> listarPorAdopcion(Integer adopcionId);

	List<RespuestaSeguimientoAdopcion> listarRespuestasPorAdopcion(Integer adopcionId);

	Adopcion obtenerAdopcionActivaAprobada(Integer adopcionId);

	boolean incumpleSeguimiento8Semanas(Integer adopcionId);

	Optional<SeguimientoPostAdopcion> findById(Integer id);

	void eliminarLogico(Integer seguimientoId, Integer usuarioId, String usuarioNombre);

	

}
