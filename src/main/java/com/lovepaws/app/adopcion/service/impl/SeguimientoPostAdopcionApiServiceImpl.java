package com.lovepaws.app.adopcion.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.adopcion.mapper.SeguimientoPostAdopcionMapper;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoPostAdopcionApiServiceImpl implements SeguimientoPostAdopcionApiService {

    private static final String ESTADO_ADOPCION_APROBADA = "APROBADA";

    private final SeguimientoAdopcionRepository seguimientoRepository;
    private final AdopcionRepository adopcionRepository;
    private final SeguimientoPostAdopcionMapper mapper;

    @Override
    @Transactional
    public SeguimientoPostAdopcionResponseDTO crearSeguimiento(SeguimientoPostAdopcionRequestDTO request, Integer gestorId) {
        Adopcion adopcion = obtenerAdopcionConfirmada(request.getAdopcionId());

        SeguimientoAdopcion seguimiento = new SeguimientoAdopcion();
        seguimiento.setAdopcion(adopcion);
        seguimiento.setFechaVisita(request.getFechaSeguimiento());
        seguimiento.setObservaciones(normalizarNotas(request.getNotas()));
        seguimiento.setEstado(mapearEstadoTracking(request.getEstadoMascota()));
        seguimiento.setActivo(Boolean.TRUE);

        if (gestorId != null) {
            Usuario gestor = new Usuario();
            gestor.setId(gestorId);
            seguimiento.setUsuarioCreacion(gestor);
        }

        SeguimientoAdopcion saved = seguimientoRepository.save(seguimiento);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(EstadoMascotaTracking estadoMascota) {
        List<SeguimientoAdopcion> data = (estadoMascota == null)
                ? seguimientoRepository.findAllWithRelationsOrderByFechaVisitaDesc()
                : seguimientoRepository.findByEstado_IdWithRelationsOrderByFechaVisitaDesc(mapper.toEstadoMascotaId(estadoMascota));

        return data.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public SeguimientoPostAdopcionResponseDTO actualizarSeguimiento(Integer seguimientoId,
                                                                    SeguimientoPostAdopcionRequestDTO request) {
        SeguimientoAdopcion existente = seguimientoRepository.findByIdWithRelationsAndDeletedAtIsNull(seguimientoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seguimiento no encontrado"));

        Adopcion adopcion = obtenerAdopcionConfirmada(request.getAdopcionId());
        existente.setAdopcion(adopcion);
        existente.setFechaVisita(request.getFechaSeguimiento());
        existente.setObservaciones(normalizarNotas(request.getNotas()));
        existente.setEstado(mapearEstadoTracking(request.getEstadoMascota()));

        SeguimientoAdopcion updated = seguimientoRepository.save(existente);
        return mapper.toDto(updated);
    }

    private Adopcion obtenerAdopcionConfirmada(Integer adopcionId) {
        Adopcion adopcion = adopcionRepository.findByIdAndDeletedAtIsNullAndActivoTrue(adopcionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adopción no encontrada"));

        String estadoProceso = adopcion.getEstado() != null ? adopcion.getEstado().getId() : null;
        if (!ESTADO_ADOPCION_APROBADA.equalsIgnoreCase(estadoProceso)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden registrar seguimientos para adopciones aprobadas");
        }
        return adopcion;
    }

    private EstadoMascota mapearEstadoTracking(EstadoMascotaTracking tracking) {
        EstadoMascota estado = new EstadoMascota();
        estado.setId(mapper.toEstadoMascotaId(tracking));
        return estado;
    }

    private String normalizarNotas(String notas) {
        if (notas == null) {
            return null;
        }
        String normalizadas = notas.trim();
        return normalizadas.isBlank() ? null : normalizadas;
    }
}
