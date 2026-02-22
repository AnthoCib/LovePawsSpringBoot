package com.lovepaws.app.adopcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;

public interface SeguimientoAdopcionRepository  extends JpaRepository<SeguimientoPostAdopcion, Integer> {

    @Query("""
            SELECT s
            FROM SeguimientoPostAdopcion s
            JOIN FETCH s.adopcion a
            JOIN FETCH a.estado estadoProceso
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado estadoMascota
            WHERE a.id = :adopcionId
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoPostAdopcion> findByAdopcionIdOrderByFechaVisitaDesc(
            @Param("adopcionId") Integer adopcionId);

    @Query("""
            SELECT s
            FROM SeguimientoPostAdopcion s
            JOIN FETCH s.adopcion a
            JOIN FETCH a.estado
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoPostAdopcion> findAllByOrderByFechaVisitaDesc();

    @Query("""
            SELECT s
            FROM SeguimientoPostAdopcion s
            JOIN FETCH s.adopcion a
            JOIN FETCH a.estado
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado
            WHERE s.estado.id = :estadoId
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoPostAdopcion> findByEstado_IdOrderByFechaVisitaDesc(
            @Param("estadoId") String estadoId);

    @Query("""
            SELECT s
            FROM SeguimientoPostAdopcion s
            JOIN FETCH s.adopcion a
            JOIN FETCH a.estado
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado
            WHERE s.id = :id
              AND s.deletedAt IS NULL
            """)
    Optional<SeguimientoPostAdopcion> findByIdAndDeletedAtIsNull(
            @Param("id") Integer id);

    @Query("""
            SELECT s
            FROM SeguimientoPostAdopcion s
            JOIN FETCH s.adopcion a
            JOIN FETCH a.estado estadoProceso
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado estadoMascota
            WHERE s.deletedAt IS NULL
              AND (:estadoMascotaId IS NULL OR estadoMascota.id = :estadoMascotaId)
              AND (:estadoProcesoId IS NULL OR estadoProceso.id = :estadoProcesoId)
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoPostAdopcion> findAllByFiltros(
            @Param("estadoMascotaId") String estadoMascotaId,
            @Param("estadoProcesoId") String estadoProcesoId);

    @Query("""
            SELECT COUNT(s)
            FROM SeguimientoPostAdopcion s
            WHERE s.adopcion.id = :adopcionId
              AND s.estado.id = :estadoId
              AND s.deletedAt IS NULL
            """)
    long countByAdopcionIdAndEstadoId(
            @Param("adopcionId") Integer adopcionId,
            @Param("estadoId") String estadoId);
}