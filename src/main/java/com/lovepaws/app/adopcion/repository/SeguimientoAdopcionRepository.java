package com.lovepaws.app.adopcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;

public interface SeguimientoAdopcionRepository extends JpaRepository<SeguimientoAdopcion, Integer> {

    List<SeguimientoAdopcion> findByAdopcionIdOrderByFechaVisitaDesc(Integer adopcionId);

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            LEFT JOIN FETCH s.estadoProceso ep
            WHERE s.adopcion.id = :adopcionId
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findByAdopcionIdWithEstadoProcesoOrderByFechaVisitaDesc(@Param("adopcionId") Integer adopcionId);

    List<SeguimientoAdopcion> findAllByOrderByFechaVisitaDesc();

    List<SeguimientoAdopcion> findByEstadoProceso_IdOrderByFechaVisitaDesc(String estadoId);

    Optional<SeguimientoAdopcion> findByIdAndDeletedAtIsNull(Integer id);

    @Override
    List<SeguimientoAdopcion> findAll();

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
