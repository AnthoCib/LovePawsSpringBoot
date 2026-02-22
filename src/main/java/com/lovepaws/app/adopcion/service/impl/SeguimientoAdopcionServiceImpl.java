package com.lovepaws.app.adopcion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.adopcion.domain.Adopcion;
import com.lovepaws.app.adopcion.domain.EstadoAdopcion;
import com.lovepaws.app.adopcion.domain.RespuestaSeguimientoAdopcion;
import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;
import com.lovepaws.app.adopcion.repository.AdopcionRepository;
import com.lovepaws.app.adopcion.repository.RespuestaSeguimientoAdopcionRepository;
import com.lovepaws.app.adopcion.repository.SeguimientoAdopcionRepository;
import com.lovepaws.app.adopcion.service.SeguimientoService;
import com.lovepaws.app.mascota.domain.EstadoMascota;
import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.mascota.repository.MascotaRepository;
import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;
import com.lovepaws.app.seguimiento.repository.EstadoSeguimientoRepository;
import com.lovepaws.app.user.domain.Usuario;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoAdopcionServiceImpl implements SeguimientoService {


    private final SeguimientoAdopcionRepository seguimientoRepo;
    private final RespuestaSeguimientoAdopcionRepository respuestaRepo;
    private final AdopcionRepository adopcionRepo;
    private final MascotaRepository mascotaRepository;
    private final AuditoriaService auditoriaService;
   

    @Override
    @Transactional
    public SeguimientoPostAdopcion createSeguimiento(SeguimientoPostAdopcion seguimiento, Integer usuarioId, String usuarioNombre) {
        SeguimientoPostAdopcion saved = seguimientoRepo.save(seguimiento);
        auditoriaService.registrar("seguimiento_post_adopcion", saved.getId(), "CREAR_SEGUIMIENTO", usuarioId,
                usuarioNombre, "Seguimiento post adopción registrado");
        return saved;
    }


    @Override
    @Transactional
    public SeguimientoPostAdopcion crearSeguimientoCompleto(Integer adopcionId,
                                                        LocalDateTime fechaVisita,
                                                        String observaciones,
                                                        String estadoMascotaId,
                                                        Integer usuarioId,
                                                        String usuarioNombre) {
        Adopcion adopcion = obtenerAdopcionActivaAprobada(adopcionId);

        if (fechaVisita == null) {
            throw new IllegalArgumentException("La fecha de visita es obligatoria");
        }

        if (adopcion.getFechaAdopcion() != null && fechaVisita.isBefore(adopcion.getFechaAdopcion())) {
            throw new IllegalStateException("La fecha de visita no puede ser menor a la fecha de adopción");
        }

        SeguimientoPostAdopcion seguimiento = new SeguimientoPostAdopcion();
        seguimiento.setAdopcion(adopcion);
        seguimiento.setFechaVisita(fechaVisita);
        seguimiento.setObservaciones(observaciones != null ? observaciones.trim() : null);
        seguimiento.setActivo(Boolean.TRUE);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        seguimiento.setUsuarioCreacion(usuario);

        if (estadoMascotaId != null && !estadoMascotaId.isBlank()) {
        	EstadoSeguimiento  estadoMascota = new EstadoSeguimiento();
            estadoMascota.setId(estadoMascotaId.trim());
            seguimiento.setEstado(estadoMascota);
        }

        SeguimientoPostAdopcion saved = seguimientoRepo.save(seguimiento);

        if (estadoMascotaId != null && !estadoMascotaId.isBlank() && adopcion.getMascota() != null && adopcion.getMascota().getId() != null) {
            Mascota mascota = mascotaRepository.findById(adopcion.getMascota().getId())
                    .orElseThrow(() -> new IllegalStateException("Mascota asociada a adopción no encontrada"));
            EstadoMascota nuevoEstado = new EstadoMascota();
            nuevoEstado.setId(estadoMascotaId.trim());
            mascota.setEstado(nuevoEstado);
            mascotaRepository.save(mascota);
        }

        auditoriaService.registrar("seguimiento_post_adopcion", saved.getId(), "INSERT", usuarioId,
                usuarioNombre, "Seguimiento post adopción creado para adopción " + adopcionId);

        return saved;
    }

    @Override
    @Transactional
    public RespuestaSeguimientoAdopcion responderSeguimiento(Integer seguimientoId,
                                                             Integer adopcionId,
                                                             String estadoSalud,
                                                             String comportamiento,
                                                             String alimentacion,
                                                             String comentarios,
                                                             Integer usuarioId,
                                                             String usuarioNombre) {
        Adopcion adopcion = obtenerAdopcionActivaAprobada(adopcionId);

        if (estadoSalud == null || estadoSalud.isBlank() || comportamiento == null || comportamiento.isBlank()
                || alimentacion == null || alimentacion.isBlank()) {
            throw new IllegalArgumentException("Estado de salud, comportamiento y alimentación son obligatorios");
        }

        if (adopcion.getUsuarioAdoptante() == null || !adopcion.getUsuarioAdoptante().getId().equals(usuarioId)) {
            throw new IllegalStateException("El adoptante autenticado no pertenece a la adopción");
        }

        SeguimientoPostAdopcion seguimiento = seguimientoRepo.findById(seguimientoId)
                .orElseThrow(() -> new IllegalArgumentException("Seguimiento no encontrado"));

        if (seguimiento.getAdopcion() == null || !seguimiento.getAdopcion().getId().equals(adopcion.getId())) {
            throw new IllegalStateException("El seguimiento no pertenece a la adopción indicada");
        }

        RespuestaSeguimientoAdopcion respuesta = new RespuestaSeguimientoAdopcion();
        respuesta.setSeguimiento(seguimiento);
        respuesta.setAdopcion(adopcion);
        respuesta.setEstadoSalud(estadoSalud.trim());
        respuesta.setComportamiento(comportamiento.trim());
        respuesta.setAlimentacion(alimentacion.trim());
        respuesta.setComentarios(comentarios != null ? comentarios.trim() : null);
        respuesta.setRevisado(Boolean.FALSE);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        respuesta.setUsuarioCreacion(usuario);

        RespuestaSeguimientoAdopcion saved = respuestaRepo.save(respuesta);

        auditoriaService.registrar("respuesta_seguimiento_adoptante", saved.getId(), "INSERT", usuarioId,
                usuarioNombre, "Respuesta registrada para seguimiento " + seguimientoId);

        return saved;
    }

    @Override
    @Transactional
    public void eliminarSeguimientoSoft(Integer seguimientoId, Integer usuarioId, String usuarioNombre) {
        SeguimientoPostAdopcion seguimiento = seguimientoRepo.findById(seguimientoId)
                .orElseThrow(() -> new IllegalArgumentException("Seguimiento no encontrado"));

        seguimientoRepo.delete(seguimiento);

        auditoriaService.registrar("seguimiento_post_adopcion", seguimientoId, "DELETE", usuarioId,
                usuarioNombre, "Soft delete de seguimiento post adopción");
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoPostAdopcion> listarPorAdopcion(Integer adopcionId) {
        return seguimientoRepo.findByAdopcionIdOrderByFechaVisitaDesc(adopcionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaSeguimientoAdopcion> listarRespuestasPorAdopcion(Integer adopcionId) {
        return respuestaRepo.findByAdopcion_IdOrderByFechaRespuestaDesc(adopcionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Adopcion obtenerAdopcionActivaAprobada(Integer adopcionId) {
        Adopcion adopcion = adopcionRepo.findById(adopcionId)
                .orElseThrow(() -> new IllegalArgumentException("Adopción no encontrada"));

        if (adopcion.getDeletedAt() != null) {
            throw new IllegalStateException("La adopción está eliminada");
        }

        if (!Boolean.TRUE.equals(adopcion.getActivo())) {
            throw new IllegalStateException("La adopción no está activa");
        }

        EstadoAdopcion estado = adopcion.getEstado();
        if (estado == null || estado.getDescripcion().equalsIgnoreCase(estado.getId())) {
            throw new IllegalStateException("Solo se permite seguimiento para adopciones aprobadas");
        }

        return adopcion;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean incumpleSeguimiento8Semanas(Integer adopcionId) {
        return adopcionRepo.existsIncumplimientoSeguimiento8SemanasByAdopcionId(adopcionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SeguimientoPostAdopcion> findById(Integer id) {
        return seguimientoRepo.findById(id);
    }


	@Override
	public void eliminarLogico(Integer seguimientoId, Integer usuarioId, String usuarioNombre) {
		// TODO Auto-generated method stub
		
	}

}
