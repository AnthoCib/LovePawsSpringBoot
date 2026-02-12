package com.lovepaws.app.api.mapper;

import com.lovepaws.app.api.dto.MascotaResponseDTO;
import com.lovepaws.app.mascota.domain.Mascota;
import org.springframework.stereotype.Component;

@Component
public class MascotaApiMapper {

    public MascotaResponseDTO toResponse(Mascota mascota) {
        return MascotaResponseDTO.builder()
                .id(mascota.getId())
                .nombre(mascota.getNombre())
                .edad(mascota.getEdad())
                .sexo(mascota.getSexo() != null ? mascota.getSexo().name() : null)
                .descripcion(mascota.getDescripcion())
                .fotoUrl(mascota.getFotoUrl())
                .categoriaId(mascota.getCategoria() != null ? mascota.getCategoria().getId() : null)
                .categoriaNombre(mascota.getCategoria() != null ? mascota.getCategoria().getNombre() : null)
                .razaId(mascota.getRaza() != null ? mascota.getRaza().getId() : null)
                .razaNombre(mascota.getRaza() != null ? mascota.getRaza().getNombre() : null)
                .estadoId(mascota.getEstado() != null ? mascota.getEstado().getId() : null)
                .estadoDescripcion(mascota.getEstado() != null ? mascota.getEstado().getDescripcion() : null)
                .usuarioCreacionId(mascota.getUsuarioCreacion() != null ? mascota.getUsuarioCreacion().getId() : null)
                .build();
    }
}
