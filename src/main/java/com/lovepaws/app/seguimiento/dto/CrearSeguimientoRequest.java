package com.lovepaws.app.seguimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearSeguimientoRequest {

    @NotNull(message = "La adopción es obligatoria")
    private Integer adopcionId;

    @NotBlank(message = "El mensaje inicial es obligatorio")
    @Size(max = 2000, message = "El mensaje inicial no puede exceder 2000 caracteres")
    private String mensajeInicial;
}
