package com.lovepaws.app.adopcion.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.dto.SeguimientoGestorItemDTO;
import com.lovepaws.app.adopcion.mapper.SeguimientoGestorMapper;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeguimientoGestorViewService {

    private final AdopcionRepository adopcionRepository;
    private final SeguimientoAdopcionRepository seguimientoRepository;
    private final SeguimientoGestorMapper mapper;

    public SeguimientoGestorViewData obtenerVista(Integer adopcionId) {
        Adopcion adopcion = adopcionRepository.findByIdWithMascota(adopcionId)
                .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));

        String mascotaNombre = (adopcion.getMascota() != null && adopcion.getMascota().getNombre() != null)
                ? adopcion.getMascota().getNombre()
                : "-";

        List<SeguimientoGestorItemDTO> seguimientos = seguimientoRepository
                .findByAdopcionIdWithEstadoProcesoOrderByFechaVisitaDesc(adopcionId)
                .stream()
                .map(mapper::toDto)
                .toList();

        return SeguimientoGestorViewData.builder()
                .adopcionId(adopcion.getId())
                .fechaAdopcion(adopcion.getFechaAdopcion())
                .mascotaNombre(mascotaNombre)
                .seguimientos(seguimientos)
                .build();
    }

    @Value
    @Builder
    public static class SeguimientoGestorViewData {
        Integer adopcionId;
        LocalDateTime fechaAdopcion;
        String mascotaNombre;
        List<SeguimientoGestorItemDTO> seguimientos;
    }
}
