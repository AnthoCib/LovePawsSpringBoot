package com.lovepaws.app.adopcion.service;

import java.util.List;

import com.lovepaws.app.adopcion.dto.SolicitudTrackingResponseDTO;

public interface SolicitudTrackingService {

    // Lista solicitudes de adopción filtradas por estado de tracking.
    List<SolicitudTrackingResponseDTO> listarPorEstado(String estado);

    // Actualiza el estado de una solicitud y devuelve el estado final para frontend.
    SolicitudTrackingResponseDTO actualizarEstado(Integer solicitudId, String estado, String comentario);
}
