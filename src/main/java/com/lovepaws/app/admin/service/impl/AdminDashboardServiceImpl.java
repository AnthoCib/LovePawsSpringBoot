package com.lovepaws.app.admin.service.impl;

import org.springframework.stereotype.Service;

import com.lovepaws.app.admin.service.AdminDashboardService;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

	private final UsuarioRepository usuarioRepo;
	private final MascotaRepository mascotaRepo;
	private final AdopcionRepository adopcionRepo;
	private final SolicitudAdopcionRepository solicitudRepo;

	@Override
	public long totalUsuarios() {
		// TODO Auto-generated method stub
		return usuarioRepo.count();
	}

	@Override
	public long totalMascotas() {
		// TODO Auto-generated method stub
		 return mascotaRepo.count();
	}

	@Override
	public long adopcionesPendientes() {
		// TODO Auto-generated method stub
		 return adopcionRepo.countByEstado_Id("PENDIENTE");
	}

	@Override
	public long solicitudesPendientes() {
		// TODO Auto-generated method stub
		 return solicitudRepo.countByEstado_Id("PENDIENTE");
	}

}
