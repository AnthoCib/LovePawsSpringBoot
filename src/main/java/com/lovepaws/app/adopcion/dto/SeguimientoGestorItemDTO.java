package com.lovepaws.app.adopcion.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SeguimientoGestorItemDTO {
    Integer id;
    LocalDateTime fechaVisita;
    String estadoId;
    String estadoDescripcion;
    String observaciones;
}
