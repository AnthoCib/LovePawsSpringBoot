package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lovepaws.app.adopcion.domain.Adopcion;

public interface AdopcionRepository  extends JpaRepository<Adopcion, Integer>{
	
	List<Adopcion> findByUsuarioAdoptante_Id(Integer idUsuarioAdoptante);

	List<Adopcion> findByMascotaId(Integer mascotaId);

	boolean existsBySolicitud_Id(Integer solicitudId);

	long countByEstado_Id(String estadoId);

	java.util.Optional<Adopcion> findByIdAndDeletedAtIsNullAndActivoTrue(Integer id);

	@Query(value = """
		SELECT a.id
		FROM adopcion a
		WHERE a.deleted_at IS NULL
		  AND a.estado_id = 'APROBADA'
		  AND NOT EXISTS (
		      SELECT 1 FROM seguimiento_post_adopcion s
		      WHERE s.adopcion_id = a.id
		        AND s.deleted_at IS NULL
		        AND s.fecha_visita <= DATE_ADD(a.fecha_adopcion, INTERVAL 56 DAY)
		  )
		""", nativeQuery = true)
	java.util.List<Integer> findAdopcionesIncumplenSeguimiento8Semanas();

	@Query(value = """
		SELECT CASE WHEN EXISTS (
		  SELECT 1
		  FROM adopcion a
		  WHERE a.id = :adopcionId
		    AND a.deleted_at IS NULL
		    AND a.estado_id = 'APROBADA'
		    AND NOT EXISTS (
		        SELECT 1 FROM seguimiento_post_adopcion s
		        WHERE s.adopcion_id = a.id
		          AND s.deleted_at IS NULL
		          AND s.fecha_visita <= DATE_ADD(a.fecha_adopcion, INTERVAL 56 DAY)
		    )
		) THEN TRUE ELSE FALSE END
		""", nativeQuery = true)
	boolean incumpleSeguimiento8Semanas(@Param("adopcionId") Integer adopcionId);

}
