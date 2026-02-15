package com.lovepaws.app.adopcion.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SeguimientoPostAdopcionRequestDTO {

    @NotNull(message = "adopcionId es obligatorio")
    private Integer adopcionId;

    @NotNull(message = "fechaSeguimiento es obligatoria")
    private LocalDateTime fechaSeguimiento;

    @Size(max = 2000, message = "notas no debe exceder 2000 caracteres")
    private String notas;

    @NotNull(message = "estadoMascota es obligatorio")
    private EstadoMascotaTracking estadoMascota;

    private Boolean activo;
}
