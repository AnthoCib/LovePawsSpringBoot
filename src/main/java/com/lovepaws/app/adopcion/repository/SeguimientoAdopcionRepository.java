package com.lovepaws.app.adopcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;


@Repository("seguimientoPostAdopcionRepositoryAdopcion")
public interface SeguimientoAdopcionRepository extends JpaRepository<SeguimientoAdopcion, Integer> {

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            JOIN FETCH s.adopcion a
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado
            WHERE a.id = :adopcionId
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findByAdopcion_IdOrderByFechaVisitaDesc(@Param("adopcionId") Integer adopcionId);

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            JOIN FETCH s.adopcion a
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findAllWithRelationsOrderByFechaVisitaDesc();

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            JOIN FETCH s.adopcion a
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado
            WHERE s.estado.id = :estadoId
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findByEstado_IdWithRelationsOrderByFechaVisitaDesc(@Param("estadoId") String estadoId);

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            JOIN FETCH s.adopcion a
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado
            WHERE s.id = :id
              AND s.deletedAt IS NULL
            """)
    Optional<SeguimientoAdopcion> findByIdWithRelationsAndDeletedAtIsNull(@Param("id") Integer id);
}
