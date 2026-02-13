package com.lovepaws.app.mascota.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RazaRequestDTO(
        @NotNull(message = "El id de especie es obligatorio")
        Integer especieId,
        @NotBlank(message = "El nombre de la raza es obligatorio")
        @Size(max = 50, message = "El nombre de la raza no puede superar 50 caracteres")
        String nombre,
        Boolean estado
) {
}
