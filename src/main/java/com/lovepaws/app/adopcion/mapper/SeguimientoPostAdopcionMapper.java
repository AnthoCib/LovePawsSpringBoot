package com.lovepaws.app.adopcion.mapper;

import org.springframework.stereotype.Component;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.seguimiento.domain.SeguimientoPostAdopcion;

@Component
public class SeguimientoPostAdopcionMapper {

    public SeguimientoPostAdopcionResponseDTO toDto(SeguimientoAdopcion seguimiento) {
        String estadoMascotaId = seguimiento.getEstado() != null ? seguimiento.getEstado().getId() : null;
        String estadoProcesoAdopcion = seguimiento.getAdopcion() != null && seguimiento.getAdopcion().getEstado() != null
                ? seguimiento.getAdopcion().getEstado().getId()
                : null;

        return SeguimientoPostAdopcionResponseDTO.builder()
                .id(seguimiento.getId())
                .adopcionId(seguimiento.getAdopcion() != null ? seguimiento.getAdopcion().getId() : null)
                .adoptanteId(seguimiento.getAdopcion() != null && seguimiento.getAdopcion().getUsuarioAdoptante() != null
                        ? seguimiento.getAdopcion().getUsuarioAdoptante().getId()
                        : null)
                .gestorId(seguimiento.getUsuarioCreacion() != null ? seguimiento.getUsuarioCreacion().getId() : null)
                .fechaSeguimiento(seguimiento.getFechaVisita())
                .notas(seguimiento.getObservaciones())
                .estadoMascota(mapearTrackingDesdeEstadoId(estadoMascotaId))
                .estadoMascotaId(estadoMascotaId)
                .estadoProcesoAdopcion(estadoProcesoAdopcion)
                .activo(seguimiento.getActivo())
                .fechaCreacion(seguimiento.getFechaCreacion())
                .fechaActualizacion(seguimiento.getFechaModificacion())
                .build();
    }
    public SeguimientoPostAdopcionResponseDTO toDto(SeguimientoPostAdopcion seguimiento) {
        if (seguimiento == null) return null;

        String estadoMascotaId = seguimiento.getEstado() != null ? seguimiento.getEstado().getId() : null;
        String estadoProcesoAdopcion = seguimiento.getAdopcion() != null && seguimiento.getAdopcion().getEstado() != null
                ? seguimiento.getAdopcion().getEstado().getId()
                : null;

        return SeguimientoPostAdopcionResponseDTO.builder()
                .id(seguimiento.getId())
                .adopcionId(seguimiento.getAdopcion() != null ? seguimiento.getAdopcion().getId() : null)
                .adoptanteId(seguimiento.getAdopcion() != null && seguimiento.getAdopcion().getUsuarioAdoptante() != null
                        ? seguimiento.getAdopcion().getUsuarioAdoptante().getId()
                        : null)
                .gestorId(seguimiento.getUsuarioCreacion() != null ? seguimiento.getUsuarioCreacion().getId() : null)
                .fechaSeguimiento(seguimiento.getFechaCreacion())
                .notas(seguimiento.getObservaciones())
                .estadoMascota(mapearTrackingDesdeEstadoId(estadoMascotaId))
                .estadoMascotaId(estadoMascotaId)
                .estadoProcesoAdopcion(estadoProcesoAdopcion)
                .activo(seguimiento.getActivo())
                .fechaCreacion(seguimiento.getFechaCreacion())
                .fechaActualizacion(seguimiento.getFechaModificacion())
                .build();
    }


    public String toEstadoMascotaId(EstadoMascotaTracking tracking) {
        return switch (tracking) {
            case BIEN -> "BIEN";
            case ATENCION_VETERINARIA -> "ATENCION_VETERINARIA";
            case RETORNADO -> "RETORNADO";
        };
    }

    public EstadoMascotaTracking mapearTrackingDesdeEstadoId(String estadoId) {
        if (estadoId == null) {
            return null;
        }

        return switch (estadoId.toUpperCase()) {
            case "BIEN", "ADOPTADA" -> EstadoMascotaTracking.BIEN;
            case "ATENCION_VETERINARIA", "NO_DISPONIBLE" -> EstadoMascotaTracking.ATENCION_VETERINARIA;
            case "RETORNADO", "DISPONIBLE" -> EstadoMascotaTracking.RETORNADO;
            default -> null;
        };
    }
}
