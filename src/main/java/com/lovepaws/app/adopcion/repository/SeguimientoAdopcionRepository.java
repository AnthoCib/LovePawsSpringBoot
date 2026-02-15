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

    List<SeguimientoAdopcion> findByEstado_IdOrderByFechaVisitaDesc(String estadoId);

    Optional<SeguimientoAdopcion> findByIdAndDeletedAtIsNull(Integer id);

    @Override
    List<SeguimientoAdopcion> findAll();

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            WHERE s.estado.id = :estado
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findByEstadoProceso_Id(@Param("estado") String estado);

    @Query("""
            SELECT s
            FROM SeguimientoAdopcion s
            WHERE s.estado.id = :estado
            ORDER BY s.fechaVisita DESC
            """)
    List<SeguimientoAdopcion> findByEstadoMascota_Id(@Param("estado") String estado);
}
