package com.lovepaws.app.adopcion.dto;

import java.time.LocalDateTime;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeguimientoPostAdopcionResponseDTO {

    private Integer id;
    private Integer adopcionId;
    private Integer adoptanteId;
    private Integer gestorId;
    private LocalDateTime fechaSeguimiento;
    private String notas;
    private SeguimientoPostAdopcion.EstadoMascotaSeguimiento estadoMascota;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
