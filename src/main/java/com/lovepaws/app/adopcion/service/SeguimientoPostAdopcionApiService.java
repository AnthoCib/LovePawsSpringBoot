package com.lovepaws.app.adopcion.service;

import java.util.List;

import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;

public interface SeguimientoPostAdopcionApiService {

    SeguimientoPostAdopcionResponseDTO crearSeguimiento(SeguimientoPostAdopcionRequestDTO request, Integer gestorId);

    List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(EstadoMascotaTracking estadoMascota, String estadoProceso);

    SeguimientoPostAdopcionResponseDTO actualizarSeguimiento(Integer seguimientoId, SeguimientoPostAdopcionRequestDTO request,
                                                             Integer gestorId);
}
