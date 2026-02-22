package com.lovepaws.app.seguimiento.service;

import java.util.List;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.seguimiento.domain.ResultadoSeguimiento;
import com.lovepaws.app.seguimiento.dto.RespuestaSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoCreateRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoResponse;

public interface SeguimientoPostAdopcionService {

    SeguimientoResponse crearSeguimiento(SeguimientoCreateRequest request, Integer usuarioId);

    SeguimientoResponse responderSeguimiento(Integer seguimientoId, RespuestaSeguimientoRequest request, Integer usuarioId);

    SeguimientoResponse cerrarSeguimiento(Integer seguimientoId, String comentario, Integer usuarioId);

    SeguimientoResponse escalarSeguimiento(Integer seguimientoId, String motivo, Integer usuarioId);

    void eliminarLogico(Integer seguimientoId, Integer usuarioId);

    SeguimientoResponse obtenerDetalle(Integer seguimientoId, Integer usuarioId, boolean gestorOAdmin);

    List<SeguimientoResponse> listarMisSeguimientos(Integer adoptanteId);

    List<SeguimientoResponse> listarSeguimientosGestion();
    
    List<ResultadoSeguimiento> listarResultados();

    List<SeguimientoAdopcion> listarSeguimientos(EstadoSeguimiento estado);
}
