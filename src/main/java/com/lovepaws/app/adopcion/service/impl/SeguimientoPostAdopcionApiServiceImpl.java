package com.lovepaws.app.adopcion.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoRepository;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;
import com.lovepaws.app.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoPostAdopcionApiServiceImpl implements SeguimientoPostAdopcionApiService {

    private final SeguimientoRepository seguimientoRepository;
    private final AdopcionRepository adopcionRepository;

    @Override
    @Transactional
    public SeguimientoPostAdopcionResponseDTO crearSeguimiento(SeguimientoPostAdopcionRequestDTO request, Integer gestorId) {
        Adopcion adopcion = obtenerAdopcionConfirmada(request.getAdopcionId());

        SeguimientoPostAdopcion seguimiento = new SeguimientoPostAdopcion();
        seguimiento.setAdopcion(adopcion);
        seguimiento.setFechaVisita(request.getFechaSeguimiento());
        seguimiento.setObservaciones(request.getNotas() != null ? request.getNotas().trim() : null);
        seguimiento.setEstadoMascota(request.getEstadoMascota());

        // Relación con Usuario: guardamos el gestor creador cuando llega su id autenticado.
        if (gestorId != null) {
            Usuario gestor = new Usuario();
            gestor.setId(gestorId);
            seguimiento.setUsuarioCreacion(gestor);
        }

        return toDto(seguimientoRepository.save(seguimiento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(
            SeguimientoPostAdopcion.EstadoMascotaSeguimiento estadoMascota) {

        List<SeguimientoPostAdopcion> data = (estadoMascota == null)
                ? seguimientoRepository.findAllByOrderByFechaVisitaDesc()
                : seguimientoRepository.findByEstadoMascotaOrderByFechaVisitaDesc(estadoMascota);

        return data.stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public SeguimientoPostAdopcionResponseDTO actualizarSeguimiento(Integer seguimientoId,
                                                                    SeguimientoPostAdopcionRequestDTO request) {
        SeguimientoPostAdopcion existente = seguimientoRepository.findById(seguimientoId)
                .orElseThrow(() -> new IllegalArgumentException("Seguimiento no encontrado"));

        Adopcion adopcion = obtenerAdopcionConfirmada(request.getAdopcionId());
        existente.setAdopcion(adopcion);
        existente.setFechaVisita(request.getFechaSeguimiento());
        existente.setObservaciones(request.getNotas() != null ? request.getNotas().trim() : null);
        existente.setEstadoMascota(request.getEstadoMascota());

        return toDto(seguimientoRepository.save(existente));
    }

    private Adopcion obtenerAdopcionConfirmada(Integer adopcionId) {
        Adopcion adopcion = adopcionRepository.findById(adopcionId)
                .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));

        String estado = adopcion.getEstado() != null ? adopcion.getEstado().getId() : null;
        // Regla solicitada: no se permite seguimiento para adopciones no confirmadas.
        if (!"APROBADA".equalsIgnoreCase(estado) && !"CONFIRMADA".equalsIgnoreCase(estado)) {
            throw new IllegalStateException("Solo se pueden registrar seguimientos para adopciones confirmadas");
        }
        return adopcion;
    }

    private SeguimientoPostAdopcionResponseDTO toDto(SeguimientoPostAdopcion s) {
        return SeguimientoPostAdopcionResponseDTO.builder()
                .id(s.getId())
                .adopcionId(s.getAdopcion() != null ? s.getAdopcion().getId() : null)
                .adoptanteId(s.getAdopcion() != null && s.getAdopcion().getUsuarioAdoptante() != null
                        ? s.getAdopcion().getUsuarioAdoptante().getId() : null)
                .gestorId(s.getUsuarioCreacion() != null ? s.getUsuarioCreacion().getId() : null)
                .fechaSeguimiento(s.getFechaVisita())
                .notas(s.getObservaciones())
                .estadoMascota(s.getEstadoMascota())
                .fechaCreacion(s.getFechaCreacion())
                .fechaActualizacion(s.getFechaModificacion())
                .build();
    }
}
