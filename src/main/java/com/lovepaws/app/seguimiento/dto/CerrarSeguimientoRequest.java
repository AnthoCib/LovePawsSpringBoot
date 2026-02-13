package com.lovepaws.app.seguimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CerrarSeguimientoRequest {

    @NotBlank(message = "El comentario de cierre es obligatorio")
    @Size(max = 2000, message = "El comentario no puede exceder 2000 caracteres")
    private String comentario;
}
