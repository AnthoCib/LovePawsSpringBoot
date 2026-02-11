package com.lovepaws.app.adopcion.service;

import java.util.List;

import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;

public interface SeguimientoPostAdopcionApiService {

    SeguimientoPostAdopcionResponseDTO crearSeguimiento(SeguimientoPostAdopcionRequestDTO request, Integer gestorId);

    List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(SeguimientoPostAdopcion.EstadoMascotaSeguimiento estadoMascota);

    SeguimientoPostAdopcionResponseDTO actualizarSeguimiento(Integer seguimientoId, SeguimientoPostAdopcionRequestDTO request);
}
