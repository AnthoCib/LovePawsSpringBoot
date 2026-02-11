package com.lovepaws.app.adopcion.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolicitudTrackingResponseDTO {

    private Integer id;
    private Integer adoptanteId;
    private Integer mascotaId;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String mensaje;
}
