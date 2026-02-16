// src/main/java/com/lovepaws/app/adopcion/service/impl/SeguimientoPostAdopcionApiServiceImpl.java
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
import com.lovepaws.app.adopcion.repository.SeguimientoPostAdopcionTrackingRepository;
import com.lovepaws.app.adopcion.service.SeguimientoPostAdopcionApiService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoPostAdopcionApiServiceImpl implements SeguimientoPostAdopcionApiService {

    private static final String ESTADO_ADOPCION_APROBADA = "APROBADA";
    private static final List<String> ESTADOS_VALIDOS_LISTADO = List.of(
            "EXCELENTE", "BUENO", "EN_OBSERVACION", "REQUIERE_ATENCION",
            "PROBLEMA_SALUD", "INCUMPLIMIENTO", "RETIRADA");

    private final SeguimientoPostAdopcionTrackingRepository seguimientoRepository;
    private final AdopcionRepository adopcionRepository;
    private final SeguimientoPostAdopcionMapper mapper;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public SeguimientoPostAdopcionResponseDTO crearSeguimiento(SeguimientoPostAdopcionRequestDTO request, Integer gestorId) {
        Adopcion adopcion = obtenerAdopcionAprobada(request.getAdopcionId());

        SeguimientoAdopcion seguimiento = new SeguimientoAdopcion();
        seguimiento.setAdopcion(adopcion);
        seguimiento.setFechaVisita(request.getFechaSeguimiento());
        seguimiento.setObservaciones(normalizarNotas(request.getNotas()));
        seguimiento.setEstado(mapearEstadoTracking(request.getEstadoMascota()));
        seguimiento.setActivo(request.getActivo() == null ? Boolean.TRUE : request.getActivo());

        if (gestorId != null) {
            Usuario gestor = new Usuario();
            gestor.setId(gestorId);
            seguimiento.setUsuarioCreacion(gestor);
        }

        SeguimientoAdopcion saved = seguimientoRepository.save(seguimiento);
        registrarAuditoria(saved, "INSERT", gestorId, "Creación de seguimiento post-adopción");
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(EstadoMascotaTracking estadoMascota,
                                                                        String estadoProceso) {
        List<SeguimientoAdopcion> data;

        if (estadoMascota == null) {
            data = seguimientoRepository.findByEstado_IdInOrderByFechaVisitaDesc(ESTADOS_VALIDOS_LISTADO);
        } else {
            String estadoMascotaId = mapper.toEstadoMascotaId(estadoMascota);
            validarEstadoPermitidoParaListado(estadoMascotaId);
            data = seguimientoRepository.findByEstado_IdOrderByFechaVisitaDesc(estadoMascotaId);
        }

        String estadoProcesoNormalizado = normalizarFiltro(estadoProceso);
        if (estadoProcesoNormalizado != null) {
            data = data.stream()
                    .filter(s -> s.getAdopcion() != null
                            && s.getAdopcion().getEstado() != null
                            && estadoProcesoNormalizado.equalsIgnoreCase(s.getAdopcion().getEstado().getId()))
                    .toList();
        }

        return data.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public SeguimientoPostAdopcionResponseDTO actualizarSeguimiento(Integer seguimientoId,
                                                                    SeguimientoPostAdopcionRequestDTO request,
                                                                    Integer gestorId) {
        SeguimientoAdopcion existente = seguimientoRepository.findById(seguimientoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seguimiento no encontrado"));

        Adopcion adopcion = obtenerAdopcionAprobada(request.getAdopcionId());
        existente.setAdopcion(adopcion);
        existente.setFechaVisita(request.getFechaSeguimiento());
        existente.setObservaciones(normalizarNotas(request.getNotas()));
        existente.setEstado(mapearEstadoTracking(request.getEstadoMascota()));
        if (request.getActivo() != null) {
            existente.setActivo(request.getActivo());
        }

        SeguimientoAdopcion updated = seguimientoRepository.save(existente);
        registrarAuditoria(updated, "UPDATE", gestorId, "Actualización de seguimiento post-adopción");
        return mapper.toDto(updated);
    }

    private Adopcion obtenerAdopcionAprobada(Integer adopcionId) {
        Adopcion adopcion = adopcionRepository.findByIdWithRelationsAndActivoTrue(adopcionId)
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

    private String normalizarFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim().toUpperCase();
    }

    private void validarEstadoPermitidoParaListado(String estadoMascotaId) {
        if (!ESTADOS_VALIDOS_LISTADO.contains(estadoMascotaId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "estadoMascota no permitido para listado: " + estadoMascotaId);
        }
    }

    private void registrarAuditoria(SeguimientoAdopcion seguimiento, String operacion, Integer gestorId, String detalle) {
        if (seguimiento == null || seguimiento.getId() == null) {
            return;
        }
        Integer usuarioId = gestorId != null ? gestorId
                : (seguimiento.getUsuarioCreacion() != null ? seguimiento.getUsuarioCreacion().getId() : null);
        auditoriaService.registrar("seguimiento_post_adopcion", seguimiento.getId(), operacion, usuarioId,
                "GESTOR", detalle + " (adopcionId=" + (seguimiento.getAdopcion() != null ? seguimiento.getAdopcion().getId() : null) + ")");
    }

	@Override
	public List<SeguimientoPostAdopcionResponseDTO> listarSeguimientos(EstadoSeguimiento estado) {
		// TODO Auto-generated method stub
		return null;
	}


}
