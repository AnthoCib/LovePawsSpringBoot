package com.lovepaws.app.admin.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lovepaws.app.admin.dto.MascotaPorEstadoDTO;
import com.lovepaws.app.admin.dto.UsuarioPorRolDTO;
import com.lovepaws.app.admin.repository.ReporteRepository;
import com.lovepaws.app.admin.service.ReporteService;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.user.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl  implements ReporteService{


    private final UsuarioRepository usuarioRepo;
    private final MascotaRepository mascotaRepo;
    private final ReporteRepository reporteRepo;
	@Override
	public Long totalUsuarios() {
		// TODO Auto-generated method stub
		 return usuarioRepo.count();
	}

	@Override
	public Long totalMascotas() {
		// TODO Auto-generated method stub
		return mascotaRepo.count();
	}


	@Override
	public List<MascotaPorEstadoDTO> mascotasPorEstado(LocalDate desde, LocalDate hasta) {
		// TODO Auto-generated method stub
		return reporteRepo.mascotasPorEstado();
	}

	@Override
	public List<UsuarioPorRolDTO> usuariosPorRol(LocalDate desde, LocalDate hasta) {
		// TODO Auto-generated method stub
		return reporteRepo.usuariosPorRol();
	}

	@Override
	public List<MascotaPorEstadoDTO> mascotasPorEstado() {
		// TODO Auto-generated method stub
		return reporteRepo.mascotasPorEstado();
	}

	@Override
	public List<UsuarioPorRolDTO> usuariosPorRol() {
		// TODO Auto-generated method stub
		return reporteRepo.usuariosPorRol();
	}

}
