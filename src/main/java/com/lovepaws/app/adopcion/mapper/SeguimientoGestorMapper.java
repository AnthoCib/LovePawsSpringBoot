package com.lovepaws.app.adopcion.mapper;

import org.springframework.stereotype.Component;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoGestorItemDTO;

@Component
public class SeguimientoGestorMapper {

    public SeguimientoGestorItemDTO toDto(SeguimientoAdopcion entity) {
        String estadoProcesoId = entity.getEstadoProceso() != null ? entity.getEstadoProceso().getId() : null;
        String estadoProcesoLabel = entity.getEstadoProceso() != null && entity.getEstadoProceso().getDescripcion() != null
                ? entity.getEstadoProceso().getDescripcion()
                : EstadoMascotaTracking.labelDesdeId(estadoProcesoId);

        String estadoMascotaId = entity.getEstadoMascota() != null ? entity.getEstadoMascota().getId() : null;
        String estadoMascotaLabel = entity.getEstadoMascota() != null && entity.getEstadoMascota().getDescripcion() != null
                ? entity.getEstadoMascota().getDescripcion()
                : EstadoMascotaTracking.labelDesdeId(estadoMascotaId);

        return SeguimientoGestorItemDTO.builder()
                .id(entity.getId())
                .adopcionId(entity.getAdopcion() != null ? entity.getAdopcion().getId() : null)
                .mascotaId(entity.getAdopcion() != null && entity.getAdopcion().getMascota() != null
                        ? entity.getAdopcion().getMascota().getId() : null)
                .mascotaNombre(entity.getAdopcion() != null && entity.getAdopcion().getMascota() != null
                        ? entity.getAdopcion().getMascota().getNombre() : null)
                .fechaVisita(entity.getFechaVisita())
                .estadoProcesoId(estadoProcesoId)
                .estadoProcesoLabel(estadoProcesoLabel)
                .estadoMascotaId(estadoMascotaId)
                .estadoMascotaLabel(estadoMascotaLabel)
                .notas(entity.getObservaciones())
                .activo(entity.getActivo())
                .usuarioCreacionId(entity.getUsuarioCreacion() != null ? entity.getUsuarioCreacion().getId() : null)
                .fechaCreacion(entity.getFechaCreacion())
                .fechaModificacion(entity.getFechaModificacion())
                .build();
    }
}
