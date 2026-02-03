package com.lovepaws.app.adopcion.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.adopcion.repository.SeguimientoRepository;
import com.lovepaws.app.adopcion.service.SeguimientoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoServiceImpl implements SeguimientoService {
	
	private final SeguimientoRepository seguimientoRepo;

	@Override
	@Transactional
	public SeguimientoPostAdopcion createSeguimiento(SeguimientoPostAdopcion seguimiento) {
		// TODO Auto-generated method stub
		return seguimientoRepo.save(seguimiento);
	}

	@Override
	public List<SeguimientoPostAdopcion> listarPorAdopcion(Integer adopcionId) {
		// TODO Auto-generated method stub
		return seguimientoRepo.findByAdopcionId(adopcionId);
	}

	@Override
	public Optional<SeguimientoPostAdopcion> findById(Integer id) {
		// TODO Auto-generated method stub
		return seguimientoRepo.findById(id);
	}

}
