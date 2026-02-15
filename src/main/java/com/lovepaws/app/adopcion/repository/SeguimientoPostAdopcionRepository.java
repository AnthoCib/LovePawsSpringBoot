package com.lovepaws.app.adopcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;

public interface SeguimientoPostAdopcionRepository extends JpaRepository<SeguimientoAdopcion, Integer> {

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
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
    List<SeguimientoAdopcion> findAllByFiltros(@Param("estadoMascotaId") String estadoMascotaId,
                                               @Param("estadoProcesoId") String estadoProcesoId);

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            JOIN FETCH s.adopcion a
            JOIN FETCH a.estado
            LEFT JOIN FETCH a.usuarioAdoptante
            LEFT JOIN FETCH s.usuarioCreacion
            LEFT JOIN FETCH s.estado
            WHERE s.id = :id
              AND s.deletedAt IS NULL
            """)
    Optional<SeguimientoAdopcion> findByIdWithRelations(@Param("id") Integer id);
}
