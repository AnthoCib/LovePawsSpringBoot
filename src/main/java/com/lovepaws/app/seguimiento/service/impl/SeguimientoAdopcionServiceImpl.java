package com.lovepaws.app.seguimiento.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.seguimiento.domain.SeguimientoAdopcion;
import com.lovepaws.app.seguimiento.domain.SeguimientoInteraccion;
import com.lovepaws.app.seguimiento.domain.TipoInteraccionSeguimiento;
import com.lovepaws.app.seguimiento.dto.CerrarSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.CrearSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.EscalarSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.ResponderSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoInteraccionResponse;
import com.lovepaws.app.seguimiento.dto.SeguimientoResponse;
import com.lovepaws.app.seguimiento.exception.InvalidSeguimientoStateException;
import com.lovepaws.app.seguimiento.exception.SeguimientoForbiddenException;
import com.lovepaws.app.seguimiento.exception.SeguimientoNotFoundException;
import com.lovepaws.app.seguimiento.repository.SeguimientoAdopcionRepository;
import com.lovepaws.app.seguimiento.repository.SeguimientoInteraccionRepository;
import com.lovepaws.app.seguimiento.service.SeguimientoAdopcionService;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SeguimientoAdopcionServiceImpl implements SeguimientoAdopcionService {

    private final SeguimientoAdopcionRepository seguimientoRepository;
    private final SeguimientoInteraccionRepository interaccionRepository;
    private final AdopcionRepository adopcionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public SeguimientoResponse crearSeguimiento(CrearSeguimientoRequest request, Integer gestorId) {
        Adopcion adopcion = adopcionRepository.findById(request.getAdopcionId())
                .orElseThrow(() -> new SeguimientoNotFoundException("Adopción no encontrada"));

        if (!"APROBADA".equalsIgnoreCase(adopcion.getEstado().getId())) {
            throw new InvalidSeguimientoStateException("Solo se puede crear seguimiento para adopciones APROBADAS");
        }

        Usuario gestor = usuarioRepository.findById(gestorId)
                .orElseThrow(() -> new SeguimientoNotFoundException("Gestor no encontrado"));

        SeguimientoAdopcion seguimiento = SeguimientoAdopcion.builder()
                .adopcion(adopcion)
                .estado(EstadoSeguimiento.ABIERTO)
                .activo(Boolean.TRUE)
                .usuarioCreacion(gestor)
                .build();

        seguimiento = seguimientoRepository.save(seguimiento);

        crearInteraccion(seguimiento, gestor, TipoInteraccionSeguimiento.GESTOR, request.getMensajeInicial());

        return toResponse(seguimiento, true);
    }

    @Override
    public SeguimientoResponse responderSeguimiento(Long seguimientoId, ResponderSeguimientoRequest request, Integer adoptanteId) {
        SeguimientoAdopcion seguimiento = obtenerSeguimientoInterno(seguimientoId);
        validarPropiedadAdoptante(seguimiento, adoptanteId);

        if (!(seguimiento.getEstado() == EstadoSeguimiento.ABIERTO || seguimiento.getEstado() == EstadoSeguimiento.ESCALADO)) {
            throw new InvalidSeguimientoStateException("Solo se puede responder un seguimiento ABIERTO o ESCALADO");
        }

        Usuario adoptante = usuarioRepository.findById(adoptanteId)
                .orElseThrow(() -> new SeguimientoNotFoundException("Adoptante no encontrado"));

        crearInteraccion(seguimiento, adoptante, TipoInteraccionSeguimiento.ADOPTANTE, request.getMensaje());
        seguimiento.setEstado(EstadoSeguimiento.RESPONDIDO);
        seguimientoRepository.save(seguimiento);

        return toResponse(seguimiento, true);
    }

    @Override
    public SeguimientoResponse cerrarSeguimiento(Long seguimientoId, CerrarSeguimientoRequest request, Integer gestorId) {
        SeguimientoAdopcion seguimiento = obtenerSeguimientoInterno(seguimientoId);

        if (seguimiento.getEstado() != EstadoSeguimiento.RESPONDIDO) {
            throw new InvalidSeguimientoStateException("No se puede cerrar un seguimiento que no esté RESPONDIDO");
        }

        Usuario gestor = usuarioRepository.findById(gestorId)
                .orElseThrow(() -> new SeguimientoNotFoundException("Gestor no encontrado"));

        seguimiento.setEstado(EstadoSeguimiento.CERRADO);
        seguimiento.setComentarioCierre(request.getComentario());
        seguimientoRepository.save(seguimiento);

        crearInteraccion(seguimiento, gestor, TipoInteraccionSeguimiento.GESTOR,
                "Seguimiento cerrado: " + request.getComentario());

        return toResponse(seguimiento, true);
    }

    @Override
    public SeguimientoResponse escalarSeguimiento(Long seguimientoId, EscalarSeguimientoRequest request, Integer gestorId) {
        SeguimientoAdopcion seguimiento = obtenerSeguimientoInterno(seguimientoId);

        if (seguimiento.getEstado() == EstadoSeguimiento.CERRADO) {
            throw new InvalidSeguimientoStateException("No se puede escalar un seguimiento CERRADO");
        }

        Usuario gestor = usuarioRepository.findById(gestorId)
                .orElseThrow(() -> new SeguimientoNotFoundException("Gestor no encontrado"));

        seguimiento.setEstado(EstadoSeguimiento.ESCALADO);
        seguimiento.setMotivoEscalamiento(request.getMotivo());
        seguimientoRepository.save(seguimiento);

        crearInteraccion(seguimiento, gestor, TipoInteraccionSeguimiento.GESTOR,
                "Seguimiento escalado: " + request.getMotivo());

        return toResponse(seguimiento, true);
    }

    @Override
    @Transactional(readOnly = true)
    public SeguimientoResponse obtenerSeguimiento(Long seguimientoId, Integer usuarioId, boolean gestor) {
        SeguimientoAdopcion seguimiento = obtenerSeguimientoInterno(seguimientoId);
        if (!gestor) {
            validarPropiedadAdoptante(seguimiento, usuarioId);
        }
        return toResponse(seguimiento, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoInteraccionResponse> obtenerHistorial(Long seguimientoId, Integer usuarioId, boolean gestor) {
        SeguimientoAdopcion seguimiento = obtenerSeguimientoInterno(seguimientoId);
        if (!gestor) {
            validarPropiedadAdoptante(seguimiento, usuarioId);
        }
        return interaccionRepository.findBySeguimiento_IdAndDeletedAtIsNullOrderByFechaCreacionAsc(seguimientoId)
                .stream().map(this::toInteraccionResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponse> listarMisSeguimientos(Integer adoptanteId) {
        return seguimientoRepository.findByAdopcion_UsuarioAdoptante_IdAndDeletedAtIsNullOrderByFechaCreacionDesc(adoptanteId)
                .stream().map(s -> toResponse(s, false)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponse> listarSeguimientosGestor() {
        return seguimientoRepository.findByDeletedAtIsNullOrderByFechaCreacionDesc()
                .stream().map(s -> toResponse(s, false)).toList();
    }

    private SeguimientoAdopcion obtenerSeguimientoInterno(Long seguimientoId) {
        return seguimientoRepository.findByIdAndDeletedAtIsNull(seguimientoId)
                .orElseThrow(() -> new SeguimientoNotFoundException("Seguimiento no encontrado"));
    }

    private void validarPropiedadAdoptante(SeguimientoAdopcion seguimiento, Integer adoptanteId) {
        Integer ownerId = seguimiento.getAdopcion().getUsuarioAdoptante().getId();
        if (!ownerId.equals(adoptanteId)) {
            throw new SeguimientoForbiddenException("No tienes permisos para responder este seguimiento");
        }
    }

    private SeguimientoInteraccion crearInteraccion(SeguimientoAdopcion seguimiento, Usuario autor,
                                                    TipoInteraccionSeguimiento tipo, String mensaje) {
        SeguimientoInteraccion interaccion = SeguimientoInteraccion.builder()
                .seguimiento(seguimiento)
                .autor(autor)
                .tipoAutor(tipo)
                .mensaje(mensaje)
                .fechaCreacion(LocalDateTime.now())
                .build();
        return interaccionRepository.save(interaccion);
    }

    private SeguimientoResponse toResponse(SeguimientoAdopcion seguimiento, boolean incluirHistorial) {
        List<SeguimientoInteraccionResponse> historial = incluirHistorial
                ? interaccionRepository.findBySeguimiento_IdAndDeletedAtIsNullOrderByFechaCreacionAsc(seguimiento.getId())
                    .stream().map(this::toInteraccionResponse).toList()
                : List.of();

        return SeguimientoResponse.builder()
                .id(seguimiento.getId())
                .adopcionId(seguimiento.getAdopcion().getId())
                .adoptanteId(seguimiento.getAdopcion().getUsuarioAdoptante().getId())
                .mascotaNombre(seguimiento.getAdopcion().getMascota().getNombre())
                .estado(seguimiento.getEstado())
                .motivoEscalamiento(seguimiento.getMotivoEscalamiento())
                .comentarioCierre(seguimiento.getComentarioCierre())
                .fechaCreacion(seguimiento.getFechaCreacion())
                .fechaModificacion(seguimiento.getFechaModificacion())
                .historial(historial)
                .build();
    }

    private SeguimientoInteraccionResponse toInteraccionResponse(SeguimientoInteraccion i) {
        return SeguimientoInteraccionResponse.builder()
                .id(i.getId())
                .autorId(i.getAutor() != null ? i.getAutor().getId() : null)
                .autorNombre(i.getAutor() != null ? i.getAutor().getNombre() : "Sistema")
                .tipoAutor(i.getTipoAutor())
                .mensaje(i.getMensaje())
                .fechaCreacion(i.getFechaCreacion())
                .build();
    }
}
