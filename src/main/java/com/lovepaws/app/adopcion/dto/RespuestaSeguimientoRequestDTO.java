package com.lovepaws.app.adopcion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RespuestaSeguimientoRequestDTO {

    @NotNull(message = "seguimientoId es obligatorio")
    private Integer seguimientoId;

    @NotNull(message = "adopcionId es obligatorio")
    private Integer adopcionId;

    @NotBlank(message = "estadoSalud es obligatorio")
    private String estadoSalud;

    @NotBlank(message = "comportamiento es obligatorio")
    private String comportamiento;

    @NotBlank(message = "alimentacion es obligatoria")
    private String alimentacion;

    private String comentarios;
}
