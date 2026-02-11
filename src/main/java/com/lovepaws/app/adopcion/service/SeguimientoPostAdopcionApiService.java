package com.lovepaws.app.adopcion.service;

import java.util.List;

import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;

public interface SeguimientoPostAdopcionApiService {

    SeguimientoPostAdopcionResponseDTO crearSeguimiento(SeguimientoPostAdopcionRequestDTO request, Integer gestorId);

    List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(EstadoMascotaTracking estadoMascota);

    SeguimientoPostAdopcionResponseDTO actualizarSeguimiento(Integer seguimientoId, SeguimientoPostAdopcionRequestDTO request);
}
