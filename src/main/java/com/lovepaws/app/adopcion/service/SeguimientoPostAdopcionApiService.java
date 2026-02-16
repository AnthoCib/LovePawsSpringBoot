package com.lovepaws.app.adopcion.service;

import java.util.List;

import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;

public interface SeguimientoPostAdopcionApiService {

    SeguimientoPostAdopcionResponseDTO crearSeguimiento(SeguimientoPostAdopcionRequestDTO request, Integer gestorId);

    List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(EstadoSeguimiento estado);

    SeguimientoPostAdopcionResponseDTO actualizarSeguimiento(Integer seguimientoId, SeguimientoPostAdopcionRequestDTO request,
                                                             Integer gestorId);

	List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(EstadoMascotaTracking tracking, String tipo);
    
}
