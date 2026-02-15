package com.lovepaws.app.adopcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lovepaws.app.adopcion.domain.Adopcion;

public interface AdopcionRepository extends JpaRepository<Adopcion, Integer> {

    List<Adopcion> findByUsuarioAdoptante_Id(Integer idUsuarioAdoptante);

    List<Adopcion> findByMascotaId(Integer mascotaId);

    boolean existsBySolicitud_Id(Integer solicitudId);


	Optional<Adopcion> findByIdAndDeletedAtIsNullAndActivoTrue(Integer id);

	@Query(value = """
		    SELECT COUNT(*)
		    FROM adopcion a
		    WHERE a.deleted_at IS NULL
		      AND a.estado_id = :estadoId
		      AND NOT EXISTS (
		          SELECT 1 FROM seguimiento_post_adopcion s
		          WHERE s.adopcion_id = a.id
		            AND s.deleted_at IS NULL
		            AND s.fecha_visita <= DATE_ADD(a.fecha_adopcion, INTERVAL 56 DAY)
		      )
		    """, nativeQuery = true)
		long countByEstado_Id(@Param("estadoId") String estadoId);

    @Query(value = """
            SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
            FROM adopcion a
            WHERE a.id = :adopcionId
              AND a.deleted_at IS NULL
              AND a.estado_id = 'APROBADA'
              AND NOT EXISTS (
                    SELECT 1
                    FROM seguimiento_post_adopcion s
                    WHERE s.adopcion_id = a.id
                      AND s.deleted_at IS NULL
                      AND s.fecha_visita <= DATE_ADD(a.fecha_adopcion, INTERVAL 56 DAY)
              )
            """, nativeQuery = true)
    boolean existsIncumplimientoSeguimiento8SemanasByAdopcionId(@Param("adopcionId") Integer adopcionId);

    @Query("""
            SELECT a
            FROM Adopcion a
            LEFT JOIN FETCH a.mascota m
            WHERE a.id = :adopcionId
              AND a.deletedAt IS NULL
            """)
    Optional<Adopcion> findByIdWithMascota(@Param("adopcionId") Integer adopcionId);

}
