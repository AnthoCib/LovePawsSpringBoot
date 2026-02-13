package com.lovepaws.app.mascota.dto;

public record RazaResponseDTO(
        Integer id,
        Integer especieId,
        String especieNombre,
        String nombre,
        Boolean estado,
        Integer idUsuarioCreacion
) {
}
