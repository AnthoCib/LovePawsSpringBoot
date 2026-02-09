package com.lovepaws.app.adopcion.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.RespuestaSeguimientoAdoptante;
import com.lovepaws.app.adopcion.repository.RespuestaSeguimientoAdoptanteRepository;
import com.lovepaws.app.adopcion.service.RespuestaSeguimientoAdoptanteService;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RespuestaSeguimientoAdoptanteServiceImpl implements RespuestaSeguimientoAdoptanteService {

	private final RespuestaSeguimientoAdoptanteRepository respuestaRepo;
	private final AuditoriaService auditoriaService;

	@Override
	@Transactional
	public RespuestaSeguimientoAdoptante crearRespuesta(RespuestaSeguimientoAdoptante respuesta, Integer usuarioId,
			String usuarioNombre) {
		RespuestaSeguimientoAdoptante saved = respuestaRepo.save(respuesta);
		auditoriaService.registrar("respuesta_seguimiento_adoptante", saved.getId(), "CREAR_RESPUESTA", usuarioId,
				usuarioNombre, "Respuesta registrada para seguimiento " +
						(saved.getSeguimiento() != null ? saved.getSeguimiento().getId() : "-"));
		return saved;
	}

	@Override
	public List<RespuestaSeguimientoAdoptante> listarPorAdopcion(Integer adopcionId) {
		return respuestaRepo.findByAdopcion_Id(adopcionId);
	}

	@Override
	public List<RespuestaSeguimientoAdoptante> listarPorSeguimiento(Integer seguimientoId) {
		return respuestaRepo.findBySeguimiento_Id(seguimientoId);
	}

	@Override
	public Optional<RespuestaSeguimientoAdoptante> buscarUltimaRespuesta(Integer seguimientoId, Integer usuarioId) {
		return respuestaRepo
				.findTopBySeguimiento_IdAndUsuarioCreacion_IdOrderByFechaRespuestaDesc(seguimientoId, usuarioId);
	}
}
