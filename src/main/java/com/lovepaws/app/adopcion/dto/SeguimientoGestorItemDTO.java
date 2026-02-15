package com.lovepaws.app.adopcion.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SeguimientoGestorItemDTO {
    Integer id;
    Integer adopcionId;
    Integer mascotaId;
    String mascotaNombre;
    LocalDateTime fechaVisita;
    String estadoProcesoId;
    String estadoProcesoLabel;
    String estadoMascotaId;
    String estadoMascotaLabel;
    String notas;
    Boolean activo;
    Integer usuarioCreacionId;
    LocalDateTime fechaCreacion;
    LocalDateTime fechaModificacion;
}
