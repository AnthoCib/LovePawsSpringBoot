package com.lovepaws.app.adopcion.mapper;

import org.springframework.stereotype.Component;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoGestorItemDTO;

@Component
public class SeguimientoGestorMapper {

    public SeguimientoGestorItemDTO toDto(SeguimientoAdopcion entity) {
        String estadoId = entity.getEstadoProceso() != null ? entity.getEstadoProceso().getId() : null;
        String estadoDescripcion = EstadoMascotaTracking.labelDesdeId(estadoId);
        return SeguimientoGestorItemDTO.builder()
                .id(entity.getId())
                .fechaVisita(entity.getFechaVisita())
                .estadoId(estadoId)
                .estadoDescripcion(estadoDescripcion)
                .observaciones(entity.getObservaciones())
                .build();
    }
}
