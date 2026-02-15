package com.lovepaws.app.adopcion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.dto.SeguimientoGestorItemDTO;
import com.lovepaws.app.adopcion.mapper.SeguimientoGestorMapper;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeguimientoGestorViewService implements SeguimientoGestorViewQueryService {

    private final AdopcionRepository adopcionRepository;
    private final SeguimientoAdopcionRepository seguimientoRepository;
    private final SeguimientoGestorMapper mapper;

    @Override
    public List<SeguimientoGestorItemDTO> listarSeguimientosDto(Integer adopcionId, String estadoProcesoId) {
        String estado = (estadoProcesoId == null || estadoProcesoId.isBlank()) ? null : estadoProcesoId.trim().toUpperCase();
        return seguimientoRepository.findByAdopcionAndEstadoProceso(adopcionId, estado)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public SeguimientoGestorViewData obtenerVista(Integer adopcionId, String estadoProcesoId) {
        Adopcion adopcion = adopcionRepository.findByIdWithMascota(adopcionId)
                .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));

        String mascotaNombre = (adopcion.getMascota() != null && adopcion.getMascota().getNombre() != null)
                ? adopcion.getMascota().getNombre()
                : "-";

        List<SeguimientoGestorItemDTO> seguimientos = listarSeguimientosDto(adopcionId, estadoProcesoId);

        return new SeguimientoGestorViewData(
                adopcion.getId(),
                adopcion.getFechaAdopcion(),
                mascotaNombre,
                seguimientos
        );
    }
}
