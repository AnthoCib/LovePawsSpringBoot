package com.lovepaws.app.mascota.mapper;

import org.springframework.stereotype.Component;

import com.lovepaws.app.api.dto.MascotaResponseDTO;
import com.lovepaws.app.mascota.domain.Mascota;

@Component
public class MascotaMapper {

    public MascotaResponseDTO toDTO(Mascota m) {

        return MascotaResponseDTO.builder()
                .id(m.getId())
                .nombre(m.getNombre())
                .edad(m.getEdad())
                .sexo(m.getSexo().name())
                .descripcion(m.getDescripcion())
                .fotoUrl(m.getFotoUrl())

                .categoriaId(
                        m.getCategoria() != null ? m.getCategoria().getId() : null
                )
                .categoriaNombre(
                        m.getCategoria() != null ? m.getCategoria().getNombre() : null
                )

                .razaId(
                        m.getRaza() != null ? m.getRaza().getId() : null
                )
                .razaNombre(
                        m.getRaza() != null ? m.getRaza().getNombre() : null
                )

                .estadoId(
                        m.getEstado() != null ? m.getEstado().getId() : null
                )
                .estadoDescripcion(
                        m.getEstado() != null ? m.getEstado().getDescripcion() : null
                )

                .usuarioCreacionId(
                        m.getUsuarioCreacion() != null
                                ? m.getUsuarioCreacion().getId()
                                : null
                )
                .build();
    }
}