package com.lovepaws.app.adopcion.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;
import com.lovepaws.app.adopcion.dto.SolicitudTrackingResponseDTO;
import com.lovepaws.app.adopcion.repository.EstadoAdopcionRepository;
import com.lovepaws.app.adopcion.repository.SolicitudAdopcionRepository;
import com.lovepaws.app.adopcion.service.SolicitudTrackingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudTrackingServiceImpl implements SolicitudTrackingService {

    private final SolicitudAdopcionRepository solicitudRepository;
    private final EstadoAdopcionRepository estadoAdopcionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudTrackingResponseDTO> listarPorEstado(String estado) {
        // Si no envían estado, listamos todas para facilitar panel de tracking.
        if (estado == null || estado.isBlank()) {
            return solicitudRepository.findAllByOrderByFechaSolicitudDesc().stream().map(this::toDto).toList();
        }

        String estadoDb = normalizarEstado(estado);
        return solicitudRepository.findByEstado_IdOrderByFechaSolicitudDesc(estadoDb).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public SolicitudTrackingResponseDTO actualizarEstado(Integer solicitudId, String estado, String comentario) {
        SolicitudAdopcion solicitud = solicitudRepository.findByIdForUpdate(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        String estadoDb = normalizarEstado(estado);

        // Validamos que el estado exista en tabla catálogo para evitar violación FK.
        if (!estadoAdopcionRepository.existsById(estadoDb)) {
            throw new IllegalArgumentException("Estado no configurado en catálogo: " + estadoDb);
        }

        EstadoAdopcion nuevoEstado = new EstadoAdopcion();
        nuevoEstado.setId(estadoDb);
        solicitud.setEstado(nuevoEstado);

        if (comentario != null && !comentario.isBlank()) {
            solicitud.setInfoAdicional(comentario.trim());
        }

        SolicitudAdopcion guardada = solicitudRepository.save(solicitud);
        return toDto(guardada);
    }

    private SolicitudTrackingResponseDTO toDto(SolicitudAdopcion s) {
        return SolicitudTrackingResponseDTO.builder()
                .id(s.getId())
                .adoptanteId(s.getUsuario() != null ? s.getUsuario().getId() : null)
                .mascotaId(s.getMascota() != null ? s.getMascota().getId() : null)
                .estado(s.getEstado() != null ? s.getEstado().getId() : null)
                .fechaCreacion(s.getFechaSolicitud())
                .fechaActualizacion(s.getFechaModificacion())
                .mensaje(s.getInfoAdicional())
                .build();
    }

    private String normalizarEstado(String estado) {
        String valor = estado == null ? "" : estado.trim().toUpperCase(Locale.ROOT);

        // Mapeo de estados de tracking solicitados a estados actuales de BD.
        return switch (valor) {
            case "ENVIADA" -> "PENDIENTE";
            case "EN_REVISION" -> "EN_REVISION";
            case "APROBADA" -> "APROBADA";
            case "RECHAZADA" -> "RECHAZADA";
            case "PENDIENTE", "CANCELADA" -> valor;
            default -> throw new IllegalArgumentException("Estado no válido: " + estado);
        };
    }
}
