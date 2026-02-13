package com.lovepaws.app.mascota.dto;

public record EspecieResponseDTO(
        Integer id,
        String nombre,
        Boolean estado,
        Integer idUsuarioCreacion
) {
}
