package com.lovepaws.app.adopcion.dto;

import java.time.LocalDateTime;

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
    private EstadoMascotaTracking estadoMascota;
}
