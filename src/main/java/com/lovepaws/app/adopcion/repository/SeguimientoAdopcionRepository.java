package com.lovepaws.app.adopcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;

public interface SeguimientoAdopcionRepository extends JpaRepository<SeguimientoAdopcion, Integer> {

    List<SeguimientoAdopcion> findByAdopcionIdOrderByFechaVisitaDesc(Integer adopcionId);

    List<SeguimientoAdopcion> findAllByOrderByFechaVisitaDesc();

    List<SeguimientoAdopcion> findByEstadoProceso_IdOrderByFechaVisitaDesc(String estadoId);

    Optional<SeguimientoAdopcion> findByIdAndDeletedAtIsNull(Integer id);

    @Override
    List<SeguimientoAdopcion> findAll();

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            JOIN FETCH s.adopcion a
            LEFT JOIN FETCH a.mascota m
            LEFT JOIN FETCH s.estadoProceso ep
            LEFT JOIN FETCH s.estadoMascota em
            LEFT JOIN FETCH s.usuarioCreacion uc
            WHERE a.id = :adopcionId
              AND (:estadoProcesoId IS NULL OR ep.id = :estadoProcesoId)
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findByAdopcionAndEstadoProceso(@Param("adopcionId") Integer adopcionId,
                                                             @Param("estadoProcesoId") String estadoProcesoId);

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            WHERE s.estadoProceso.id = :estado
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findByEstadoProceso_Id(@Param("estado") String estado);

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            WHERE s.estadoMascota.id = :estado
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findByEstadoMascota_Id(@Param("estado") String estado);
}
