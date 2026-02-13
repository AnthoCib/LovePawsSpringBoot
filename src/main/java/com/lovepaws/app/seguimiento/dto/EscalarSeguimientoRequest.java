package com.lovepaws.app.seguimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EscalarSeguimientoRequest {

    @NotBlank(message = "El motivo de escalamiento es obligatorio")
    @Size(max = 2000, message = "El motivo no puede exceder 2000 caracteres")
    private String motivo;
}
