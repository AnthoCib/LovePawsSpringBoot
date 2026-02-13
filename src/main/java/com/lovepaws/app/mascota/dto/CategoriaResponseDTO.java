package com.lovepaws.app.mascota.dto;

public record CategoriaResponseDTO(
        Integer id,
        String nombre,
        String descripcion,
        Boolean estado,
        Integer idUsuarioCreacion
) {
}
