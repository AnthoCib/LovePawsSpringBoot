package com.lovepaws.app.mascota.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecieRequestDTO(
        @NotBlank(message = "El nombre de la especie es obligatorio")
        @Size(max = 50, message = "El nombre de la especie no puede superar 50 caracteres")
        String nombre,
        Boolean estado
) {
}
