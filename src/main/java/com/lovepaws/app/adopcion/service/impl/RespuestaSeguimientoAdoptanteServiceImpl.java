package com.lovepaws.app.adopcion.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.RespuestaSeguimientoAdoptante;
import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoRequestDTO;
import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoResponseDTO;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.RespuestaSeguimientoAdoptanteRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;
import com.lovepaws.app.adopcion.service.RespuestaSeguimientoAdoptanteService;
import com.lovepaws.app.config.storage.FileStorageService;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RespuestaSeguimientoAdoptanteServiceImpl implements RespuestaSeguimientoAdoptanteService {

    @Override
    @Transactional(readOnly = true)
    public java.util.List<RespuestaSeguimientoResponseDTO> listarPorAdopcion(Integer adopcionId) {
        return respuestaRepository.findByAdopcion_Id(adopcionId).stream().map(this::toDto).toList();
    }

    private final RespuestaSeguimientoAdoptanteRepository respuestaRepository;
    private final SeguimientoAdopcionRepository seguimientoRepository;
    private final AdopcionRepository adopcionRepository;
    private final FileStorageService fileStorageService;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public RespuestaSeguimientoResponseDTO registrarRespuesta(RespuestaSeguimientoRequestDTO request,
                                                              MultipartFile foto,
                                                              Integer usuarioId,
                                                              String usuarioNombre) {
        // Validación en capa service: seguimiento debe existir.
        SeguimientoPostAdopcion seguimiento = seguimientoRepository.findById(request.getSeguimientoId())
                .orElseThrow(() -> new IllegalArgumentException("Seguimiento no encontrado"));

        // Validación en capa service: adopción debe existir y coincidir con seguimiento.
        Adopcion adopcion = adopcionRepository.findById(request.getAdopcionId())
                .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));

        if (seguimiento.getAdopcion() == null || !seguimiento.getAdopcion().getId().equals(adopcion.getId())) {
            throw new IllegalStateException("El seguimiento no pertenece a la adopción enviada");
        }

        RespuestaSeguimientoAdoptante respuesta = new RespuestaSeguimientoAdoptante();
        respuesta.setSeguimiento(seguimiento);
        respuesta.setAdopcion(adopcion);
        respuesta.setEstadoSalud(request.getEstadoSalud().trim());
        respuesta.setComportamiento(request.getComportamiento().trim());
        respuesta.setAlimentacion(request.getAlimentacion().trim());
        respuesta.setComentarios(request.getComentarios() != null ? request.getComentarios().trim() : null);

        if (foto != null && !foto.isEmpty()) {
            respuesta.setFotoUrl(fileStorageService.store(foto));
        }

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        respuesta.setUsuarioCreacion(usuario);

        RespuestaSeguimientoAdoptante saved = respuestaRepository.save(respuesta);
        auditoriaService.registrar("respuesta_seguimiento_adoptante", saved.getId(), "INSERT", usuarioId,
                usuarioNombre, "Registro de respuesta del adoptante al seguimiento");
        return toDto(saved);
    }

    @Override
    @Transactional
    public RespuestaSeguimientoResponseDTO marcarRevisado(Integer respuestaId,
                                                          boolean revisado,
                                                          Integer usuarioId,
                                                          String usuarioNombre) {
        RespuestaSeguimientoAdoptante respuesta = respuestaRepository.findById(respuestaId)
                .orElseThrow(() -> new IllegalArgumentException("Respuesta no encontrada"));

        respuesta.setRevisado(revisado);
        RespuestaSeguimientoAdoptante updated = respuestaRepository.save(respuesta);

        auditoriaService.registrar("respuesta_seguimiento_adoptante", updated.getId(), "UPDATE", usuarioId,
                usuarioNombre, "Campo revisado actualizado a " + revisado);

        return toDto(updated);
    }

    private RespuestaSeguimientoResponseDTO toDto(RespuestaSeguimientoAdoptante r) {
        return RespuestaSeguimientoResponseDTO.builder()
                .id(r.getId())
                .seguimientoId(r.getSeguimiento() != null ? r.getSeguimiento().getId() : null)
                .adopcionId(r.getAdopcion() != null ? r.getAdopcion().getId() : null)
                .fechaRespuesta(r.getFechaRespuesta())
                .estadoSalud(r.getEstadoSalud())
                .comportamiento(r.getComportamiento())
                .alimentacion(r.getAlimentacion())
                .comentarios(r.getComentarios())
                .fotoUrl(r.getFotoUrl())
                .revisado(r.getRevisado())
                .usuarioCreacionId(r.getUsuarioCreacion() != null ? r.getUsuarioCreacion().getId() : null)
                .build();
    }
}
