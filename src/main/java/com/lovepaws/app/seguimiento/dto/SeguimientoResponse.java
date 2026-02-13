package com.lovepaws.app.seguimiento.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeguimientoResponse {
    private Long id;
    private Integer adopcionId;
    private Integer adoptanteId;
    private String mascotaNombre;
    private EstadoSeguimiento estado;
    private String motivoEscalamiento;
    private String comentarioCierre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private List<SeguimientoInteraccionResponse> historial;
}
