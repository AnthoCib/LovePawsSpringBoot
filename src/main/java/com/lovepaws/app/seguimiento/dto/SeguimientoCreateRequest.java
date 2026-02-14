package com.lovepaws.app.seguimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SeguimientoCreateRequest {

    @NotNull(message = "La adopción es obligatoria")
    private Integer adopcionId;

    @NotBlank(message = "La observación inicial es obligatoria")
    @Size(max = 2000, message = "La observación no puede exceder 2000 caracteres")
    private String observacionInicial;
}
