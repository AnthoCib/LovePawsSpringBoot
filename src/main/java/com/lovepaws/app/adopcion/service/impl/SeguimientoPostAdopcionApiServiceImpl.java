package com.lovepaws.app.adopcion.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;
import com.lovepaws.app.adopcion.dto.EstadoMascotaTracking;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionRequestDTO;
import com.lovepaws.app.adopcion.dto.SeguimientoPostAdopcionResponseDTO;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoPostAdopcionApiServiceImpl implements SeguimientoPostAdopcionApiService {

    private final SeguimientoAdopcionRepository seguimientoRepository;
    private final AdopcionRepository adopcionRepository;

    @Override
    @Transactional
    public SeguimientoPostAdopcionResponseDTO crearSeguimiento(SeguimientoPostAdopcionRequestDTO request, Integer gestorId) {
        Adopcion adopcion = obtenerAdopcionConfirmada(request.getAdopcionId());

        SeguimientoAdopcion seguimiento = new SeguimientoAdopcion();
        seguimiento.setAdopcion(adopcion);
        seguimiento.setFechaVisita(request.getFechaSeguimiento());
        seguimiento.setObservaciones(request.getNotas() != null ? request.getNotas().trim() : null);
        seguimiento.setEstado(mapearEstadoTracking(request.getEstadoMascota()));

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
    public List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(EstadoMascotaTracking estadoMascota) {

        List<SeguimientoAdopcion> data = (estadoMascota == null)
                ? seguimientoRepository.findAllByOrderByFechaVisitaDesc()
                : seguimientoRepository.findByEstadoProceso_IdOrderByFechaVisitaDesc(mapearEstadoId(estadoMascota));

        return data.stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public SeguimientoPostAdopcionResponseDTO actualizarSeguimiento(Integer seguimientoId,
                                                                    SeguimientoPostAdopcionRequestDTO request) {
        SeguimientoAdopcion existente = seguimientoRepository.findById(seguimientoId)
                .orElseThrow(() -> new IllegalArgumentException("Seguimiento no encontrado"));

        Adopcion adopcion = obtenerAdopcionConfirmada(request.getAdopcionId());
        existente.setAdopcion(adopcion);
        existente.setFechaVisita(request.getFechaSeguimiento());
        existente.setObservaciones(request.getNotas() != null ? request.getNotas().trim() : null);
        existente.setEstado(mapearEstadoTracking(request.getEstadoMascota()));

        return toDto(seguimientoRepository.save(existente));
    }

    private Adopcion obtenerAdopcionConfirmada(Integer adopcionId) {
        Adopcion adopcion = adopcionRepository.findById(adopcionId)
                .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));

        String estado = adopcion.getEstado() != null ? adopcion.getEstado().getId() : null;
        // Con base en el catálogo actual de BD solo APROBADA es estado confirmado para seguimiento.
        if (!"APROBADA".equalsIgnoreCase(estado)) {
            throw new IllegalStateException("Solo se pueden registrar seguimientos para adopciones aprobadas");
        }
        return adopcion;
    }

    private EstadoSeguimiento mapearEstadoTracking(EstadoMascotaTracking tracking) {
        EstadoSeguimiento estado = new EstadoSeguimiento();
        estado.setId(mapearEstadoId(tracking));
        return estado;
    }

    // El tracking usa IDs del catálogo estado_seguimiento en BD.
    private String mapearEstadoId(EstadoMascotaTracking tracking) {
        return switch (tracking) {
            case ABIERTO -> "ABIERTO";
            case RESPONDIDO -> "RESPONDIDO";
            case CERRADO -> "CERRADO";
            case ESCALADO -> "ESCALADO";
            case EXCELENTE -> "EXCELENTE";
            case BUENO -> "BUENO";
            case EN_OBSERVACION -> "EN_OBSERVACION";
            case REQUIERE_ATENCION -> "REQUIERE_ATENCION";
            case PROBLEMA_SALUD -> "PROBLEMA_SALUD";
            case INCUMPLIMIENTO -> "INCUMPLIMIENTO";
            case RETIRADA -> "RETIRADA";
        };
    }

    private EstadoMascotaTracking mapearTrackingDesdeEstadoId(String estadoId) {
        if (estadoId == null) {
            return null;
        }

        return switch (estadoId.toUpperCase()) {
            case "ABIERTO" -> EstadoMascotaTracking.ABIERTO;
            case "RESPONDIDO" -> EstadoMascotaTracking.RESPONDIDO;
            case "CERRADO" -> EstadoMascotaTracking.CERRADO;
            case "ESCALADO" -> EstadoMascotaTracking.ESCALADO;
            case "EXCELENTE" -> EstadoMascotaTracking.EXCELENTE;
            case "BUENO" -> EstadoMascotaTracking.BUENO;
            case "EN_OBSERVACION" -> EstadoMascotaTracking.EN_OBSERVACION;
            case "REQUIERE_ATENCION" -> EstadoMascotaTracking.REQUIERE_ATENCION;
            case "PROBLEMA_SALUD" -> EstadoMascotaTracking.PROBLEMA_SALUD;
            case "INCUMPLIMIENTO" -> EstadoMascotaTracking.INCUMPLIMIENTO;
            case "RETIRADA" -> EstadoMascotaTracking.RETIRADA;
            default -> null;
        };
    }

    private SeguimientoPostAdopcionResponseDTO toDto(SeguimientoAdopcion s) {
        String estadoId = s.getEstado() != null ? s.getEstado().getId() : null;
        return SeguimientoPostAdopcionResponseDTO.builder()
                .id(s.getId())
                .adopcionId(s.getAdopcion() != null ? s.getAdopcion().getId() : null)
                .adoptanteId(s.getAdopcion() != null && s.getAdopcion().getUsuarioAdoptante() != null
                        ? s.getAdopcion().getUsuarioAdoptante().getId() : null)
                .gestorId(s.getUsuarioCreacion() != null ? s.getUsuarioCreacion().getId() : null)
                .fechaSeguimiento(s.getFechaVisita())
                .notas(s.getObservaciones())
                .estadoMascota(mapearTrackingDesdeEstadoId(estadoId))
                .estadoMascotaId(estadoId)
                .fechaCreacion(s.getFechaCreacion())
                .fechaActualizacion(s.getFechaModificacion())
                .build();
    }
}
