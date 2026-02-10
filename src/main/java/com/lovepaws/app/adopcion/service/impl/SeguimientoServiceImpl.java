package com.lovepaws.app.adopcion.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.adopcion.repository.SeguimientoRepository;
import com.lovepaws.app.adopcion.service.SeguimientoService;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoServiceImpl implements SeguimientoService {
	
	private final SeguimientoRepository seguimientoRepo;
	private final AuditoriaService auditoriaService;

	@Override
	@Transactional
	public SeguimientoPostAdopcion createSeguimiento(SeguimientoPostAdopcion seguimiento, Integer usuarioId, String usuarioNombre) {
		SeguimientoPostAdopcion saved = seguimientoRepo.save(seguimiento);
		auditoriaService.registrar("seguimiento_post_adopcion", saved.getId(), "CREAR_SEGUIMIENTO", usuarioId,
				usuarioNombre, "Seguimiento post adopción registrado");
		return saved;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SeguimientoPostAdopcion> listarPorAdopcion(Integer adopcionId) {
		return seguimientoRepo.findByAdopcionIdOrderByFechaVisitaDesc(adopcionId);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SeguimientoPostAdopcion> findById(Integer id) {
		return seguimientoRepo.findById(id);
	}

}
