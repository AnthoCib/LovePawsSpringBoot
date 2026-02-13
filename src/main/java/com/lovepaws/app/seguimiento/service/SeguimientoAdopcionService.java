package com.lovepaws.app.seguimiento.service;

import java.util.List;

import com.lovepaws.app.seguimiento.dto.CerrarSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.CrearSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.EscalarSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.ResponderSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoInteraccionResponse;
import com.lovepaws.app.seguimiento.dto.SeguimientoResponse;

public interface SeguimientoAdopcionService {

    SeguimientoResponse crearSeguimiento(CrearSeguimientoRequest request, Integer gestorId);

    SeguimientoResponse responderSeguimiento(Long seguimientoId, ResponderSeguimientoRequest request, Integer adoptanteId);

    SeguimientoResponse cerrarSeguimiento(Long seguimientoId, CerrarSeguimientoRequest request, Integer gestorId);

    SeguimientoResponse escalarSeguimiento(Long seguimientoId, EscalarSeguimientoRequest request, Integer gestorId);

    SeguimientoResponse obtenerSeguimiento(Long seguimientoId, Integer usuarioId, boolean gestor);

    List<SeguimientoInteraccionResponse> obtenerHistorial(Long seguimientoId, Integer usuarioId, boolean gestor);

    List<SeguimientoResponse> listarMisSeguimientos(Integer adoptanteId);

    List<SeguimientoResponse> listarSeguimientosGestor();
}
