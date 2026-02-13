package com.lovepaws.app.seguimiento.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.seguimiento.domain.RespuestaSeguimientoPostAdopcion;
import com.lovepaws.app.seguimiento.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.seguimiento.dto.RespuestaSeguimientoRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoCreateRequest;
import com.lovepaws.app.seguimiento.dto.SeguimientoResponse;
import com.lovepaws.app.seguimiento.exception.EstadoInvalidoException;
import com.lovepaws.app.seguimiento.exception.SeguimientoException;
import com.lovepaws.app.seguimiento.repository.EstadoSeguimientoRepository;
import com.lovepaws.app.seguimiento.repository.RespuestaSeguimientoPostAdopcionRepository;
import com.lovepaws.app.seguimiento.repository.SeguimientoPostAdopcionRepository;
import com.lovepaws.app.seguimiento.service.SeguimientoService;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.repository.UsuarioRepository;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SeguimientoPostAdopcionServiceImpl implements SeguimientoService {

    private final SeguimientoPostAdopcionRepository seguimientoRepository;
    private final RespuestaSeguimientoPostAdopcionRepository respuestaRepository;
    private final EstadoSeguimientoRepository estadoRepository;
    private final AdopcionRepository adopcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    @Override
    public SeguimientoResponse crearSeguimiento(SeguimientoCreateRequest request, Integer usuarioId) {
        Usuario gestor = obtenerUsuario(usuarioId);
        validarGestorOAdmin(gestor);

        Adopcion adopcion = adopcionRepository.findById(request.getAdopcionId())
                .orElseThrow(() -> new SeguimientoException("Adopción no encontrada"));

        if (!"APROBADA".equalsIgnoreCase(adopcion.getEstado().getId())) {
            throw new EstadoInvalidoException("Solo se puede crear seguimiento para adopciones APROBADAS");
        }

        SeguimientoPostAdopcion seguimiento = SeguimientoPostAdopcion.builder()
                .adopcion(adopcion)
                .estado(obtenerEstado(EstadoSeguimiento.ABIERTO))
                .observaciones(request.getObservacionInicial())
                .activo(Boolean.TRUE)
                .usuarioCreacion(gestor)
                .build();

        seguimiento = seguimientoRepository.save(seguimiento);
        registrarMensaje(seguimiento, gestor, request.getObservacionInicial(), Boolean.TRUE);
        registrarAuditoria("seguimiento_post_adopcion", seguimiento.getId(), "INSERT", gestor,
                "Creación de seguimiento en estado ABIERTO");

        return mapSeguimiento(seguimiento, true);
    }

    @Override
    public SeguimientoResponse responderSeguimiento(Integer seguimientoId, RespuestaSeguimientoRequest request, Integer usuarioId) {
        SeguimientoPostAdopcion seguimiento = obtenerSeguimientoVigente(seguimientoId);
        Usuario adoptante = obtenerUsuario(usuarioId);

        validarEsAdoptanteDueno(seguimiento, adoptante);
        validarTransicionRespuesta(seguimiento.getEstado().getId());

        registrarMensaje(seguimiento, adoptante, request.getMensaje(), Boolean.FALSE);

        seguimiento.setEstado(obtenerEstado(EstadoSeguimiento.RESPONDIDO));
        seguimientoRepository.save(seguimiento);

        registrarAuditoria("respuesta_seguimiento_adoptante", seguimiento.getId(), "INSERT", adoptante,
                "Adoptante respondió seguimiento");
        registrarAuditoria("seguimiento_post_adopcion", seguimiento.getId(), "UPDATE_ESTADO", adoptante,
                "Cambio de estado a RESPONDIDO");

        return mapSeguimiento(seguimiento, true);
    }

    @Override
    public SeguimientoResponse cerrarSeguimiento(Integer seguimientoId, String comentario, Integer usuarioId) {
        SeguimientoPostAdopcion seguimiento = obtenerSeguimientoVigente(seguimientoId);
        Usuario gestor = obtenerUsuario(usuarioId);
        validarGestorOAdmin(gestor);

        validarPuedeCerrar(seguimiento);

        seguimiento.setEstado(obtenerEstado(EstadoSeguimiento.CERRADO));
        seguimiento.setObservaciones(comentario);
        seguimientoRepository.save(seguimiento);

        registrarMensaje(seguimiento, gestor, "CIERRE: " + comentario, Boolean.TRUE);
        registrarAuditoria("seguimiento_post_adopcion", seguimiento.getId(), "UPDATE_ESTADO", gestor,
                "Cambio de estado a CERRADO");

        return mapSeguimiento(seguimiento, true);
    }

    @Override
    public SeguimientoResponse escalarSeguimiento(Integer seguimientoId, String motivo, Integer usuarioId) {
        SeguimientoPostAdopcion seguimiento = obtenerSeguimientoVigente(seguimientoId);
        Usuario gestor = obtenerUsuario(usuarioId);
        validarGestorOAdmin(gestor);

        validarPuedeEscalar(seguimiento);

        seguimiento.setEstado(obtenerEstado(EstadoSeguimiento.ESCALADO));
        seguimiento.setObservaciones(motivo);
        seguimientoRepository.save(seguimiento);

        registrarMensaje(seguimiento, gestor, "ESCALAMIENTO: " + motivo, Boolean.TRUE);
        registrarAuditoria("seguimiento_post_adopcion", seguimiento.getId(), "UPDATE_ESTADO", gestor,
                "Cambio de estado a ESCALADO");

        return mapSeguimiento(seguimiento, true);
    }

    @Override
    public void eliminarLogico(Integer seguimientoId, Integer usuarioId) {
        SeguimientoPostAdopcion seguimiento = obtenerSeguimientoVigente(seguimientoId);
        Usuario gestor = obtenerUsuario(usuarioId);
        validarGestorOAdmin(gestor);

        seguimiento.setDeletedAt(LocalDateTime.now());
        seguimiento.setActivo(Boolean.FALSE);
        seguimientoRepository.save(seguimiento);

        registrarAuditoria("seguimiento_post_adopcion", seguimiento.getId(), "DELETE_LOGICO", gestor,
                "Eliminación lógica de seguimiento");
    }

    @Override
    @Transactional(readOnly = true)
    public SeguimientoResponse obtenerDetalle(Integer seguimientoId, Integer usuarioId, boolean gestorOAdmin) {
        SeguimientoPostAdopcion seguimiento = obtenerSeguimientoVigente(seguimientoId);
        if (!gestorOAdmin) {
            Usuario u = obtenerUsuario(usuarioId);
            validarEsAdoptanteDueno(seguimiento, u);
        }
        return mapSeguimiento(seguimiento, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponse> listarMisSeguimientos(Integer adoptanteId) {
        return seguimientoRepository.findByAdopcion_UsuarioAdoptante_IdAndDeletedAtIsNullOrderByFechaCreacionDesc(adoptanteId)
                .stream().map(s -> mapSeguimiento(s, false)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponse> listarSeguimientosGestion() {
        return seguimientoRepository.findByDeletedAtIsNullOrderByFechaCreacionDesc()
                .stream().map(s -> mapSeguimiento(s, false)).toList();
    }

    private void validarPuedeCerrar(SeguimientoPostAdopcion seguimiento) {
        if (!EstadoSeguimiento.RESPONDIDO.equalsIgnoreCase(seguimiento.getEstado().getId())) {
            throw new EstadoInvalidoException("No se puede cerrar si el seguimiento no está RESPONDIDO");
        }
        if (!respuestaRepository.existsBySeguimiento_IdAndDeletedAtIsNull(seguimiento.getId())) {
            throw new EstadoInvalidoException("No se puede cerrar un seguimiento sin respuestas del adoptante");
        }
    }

    private void validarPuedeEscalar(SeguimientoPostAdopcion seguimiento) {
        if (!EstadoSeguimiento.RESPONDIDO.equalsIgnoreCase(seguimiento.getEstado().getId())) {
            throw new EstadoInvalidoException("Solo se puede escalar desde estado RESPONDIDO");
        }
    }

    private void validarTransicionRespuesta(String estadoActual) {
        if (EstadoSeguimiento.CERRADO.equalsIgnoreCase(estadoActual) || EstadoSeguimiento.ESCALADO.equalsIgnoreCase(estadoActual)) {
            throw new EstadoInvalidoException("No se puede responder un seguimiento CERRADO o ESCALADO");
        }
        if (!EstadoSeguimiento.ABIERTO.equalsIgnoreCase(estadoActual)) {
            throw new EstadoInvalidoException("Solo se puede responder un seguimiento en estado ABIERTO");
        }
    }

    private void validarEsAdoptanteDueno(SeguimientoPostAdopcion seguimiento, Usuario adoptante) {
        Integer ownerId = seguimiento.getAdopcion().getUsuarioAdoptante().getId();
        if (!ownerId.equals(adoptante.getId())) {
            throw new SeguimientoException("Solo el adoptante dueño de la adopción puede responder");
        }
    }

    private void validarGestorOAdmin(Usuario usuario) {
        String rol = usuario.getRol() != null ? usuario.getRol().getNombre() : "";
        if (!("GESTOR".equalsIgnoreCase(rol) || "ADMIN".equalsIgnoreCase(rol))) {
            throw new SeguimientoException("Solo ROLE_GESTOR o ROLE_ADMIN puede ejecutar esta operación");
        }
    }

    private Usuario obtenerUsuario(Integer usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new SeguimientoException("Usuario no encontrado"));
    }

    private SeguimientoPostAdopcion obtenerSeguimientoVigente(Integer seguimientoId) {
        return seguimientoRepository.findByIdAndDeletedAtIsNull(seguimientoId)
                .orElseThrow(() -> new SeguimientoException("Seguimiento no encontrado"));
    }

    private EstadoSeguimiento obtenerEstado(String estadoId) {
        return estadoRepository.findById(estadoId)
                .orElseThrow(() -> new SeguimientoException("Estado de seguimiento no encontrado: " + estadoId));
    }

    private void registrarMensaje(SeguimientoPostAdopcion seguimiento, Usuario autor, String mensaje, Boolean revisado) {
        RespuestaSeguimientoPostAdopcion respuesta = RespuestaSeguimientoPostAdopcion.builder()
                .seguimiento(seguimiento)
                .mensaje(mensaje)
                .revisado(revisado)
                .usuarioCreacion(autor)
                .build();
        respuestaRepository.save(respuesta);
    }

    private void registrarAuditoria(String tabla, Integer idRegistro, String operacion, Usuario usuario, String detalle) {
        auditoriaService.registrar(tabla, idRegistro, operacion, usuario.getId(), usuario.getNombre(), detalle);
    }

    private SeguimientoResponse mapSeguimiento(SeguimientoPostAdopcion s, boolean includeHistorial) {
        List<SeguimientoResponse.RespuestaItem> historial = includeHistorial
                ? respuestaRepository.findBySeguimiento_IdAndDeletedAtIsNullOrderByFechaCreacionAsc(s.getId())
                    .stream()
                    .map(r -> SeguimientoResponse.RespuestaItem.builder()
                            .id(r.getId())
                            .autorId(r.getUsuarioCreacion() != null ? r.getUsuarioCreacion().getId() : null)
                            .autorNombre(r.getUsuarioCreacion() != null ? r.getUsuarioCreacion().getNombre() : "Sistema")
                            .mensaje(r.getMensaje())
                            .revisado(r.getRevisado())
                            .fechaCreacion(r.getFechaCreacion())
                            .build())
                    .toList()
                : List.of();

        return SeguimientoResponse.builder()
                .id(s.getId())
                .adopcionId(s.getAdopcion().getId())
                .adoptanteId(s.getAdopcion().getUsuarioAdoptante().getId())
                .mascotaNombre(s.getAdopcion().getMascota().getNombre())
                .estado(s.getEstado().getId())
                .observaciones(s.getObservaciones())
                .fechaCreacion(s.getFechaCreacion())
                .fechaModificacion(s.getFechaModificacion())
                .historial(historial)
                .build();
    }
}
