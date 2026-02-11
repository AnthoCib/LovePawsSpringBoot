package com.lovepaws.app.adopcion.dto;

import java.time.LocalDateTime;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeguimientoPostAdopcionRequestDTO {

    @NotNull(message = "adopcionId es obligatorio")
    private Integer adopcionId;

    @NotNull(message = "fechaSeguimiento es obligatoria")
    private LocalDateTime fechaSeguimiento;

    private String notas;

    @NotNull(message = "estadoMascota es obligatorio")
    private SeguimientoPostAdopcion.EstadoMascotaSeguimiento estadoMascota;
}
