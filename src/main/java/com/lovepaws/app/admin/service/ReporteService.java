package com.lovepaws.app.admin.service;

import java.time.LocalDate;
import java.util.List;

import com.lovepaws.app.admin.dto.MascotaPorEstadoDTO;
import com.lovepaws.app.admin.dto.UsuarioPorRolDTO;

public interface ReporteService {

	Long totalUsuarios();

	Long totalMascotas();

	List<MascotaPorEstadoDTO> mascotasPorEstado(LocalDate desde, LocalDate hasta);

	List<UsuarioPorRolDTO> usuariosPorRol(LocalDate desde, LocalDate hasta);
	
	
    List<MascotaPorEstadoDTO> mascotasPorEstado();
    List<UsuarioPorRolDTO> usuariosPorRol();
}
