package com.lovepaws.app.adopcion.service;

import java.time.LocalDateTime;
import java.util.List;

import com.lovepaws.app.adopcion.dto.SeguimientoGestorItemDTO;

public interface SeguimientoGestorViewQueryService {

    List<SeguimientoGestorItemDTO> listarSeguimientosDto(Integer adopcionId, String estadoProcesoId);

    SeguimientoGestorViewData obtenerVista(Integer adopcionId, String estadoProcesoId);

    record SeguimientoGestorViewData(
            Integer adopcionId,
            LocalDateTime fechaAdopcion,
            String mascotaNombre,
            List<SeguimientoGestorItemDTO> seguimientos
    ) {
    }
}
