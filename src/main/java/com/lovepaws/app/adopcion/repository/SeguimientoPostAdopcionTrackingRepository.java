package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;

/**
 * Repositorio de tracking post-adopción (módulo adopción).
 * Se renombra para evitar conflicto de bean con
 * com.lovepaws.app.seguimiento.repository.SeguimientoPostAdopcionRepository.
 */
public interface SeguimientoPostAdopcionTrackingRepository extends JpaRepository<SeguimientoAdopcion, Integer> {

    List<SeguimientoAdopcion> findAllByOrderByFechaVisitaDesc();

    List<SeguimientoAdopcion> findByEstado_IdOrderByFechaVisitaDesc(String estadoId);

    List<SeguimientoAdopcion> findByEstado_IdInOrderByFechaVisitaDesc(List<String> ids);
}
