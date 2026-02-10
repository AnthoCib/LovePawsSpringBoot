package com.lovepaws.app.mascota.repository;

import com.lovepaws.app.mascota.domain.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

	@Query("""
			SELECT m
			FROM Mascota m
			JOIN FETCH m.estadoMascota
			""")
	List<Mascota> findByEstado_Id(String estadoId);

	@Query("""
			SELECT m FROM Mascota m
			WHERE m.estado.id = 'DISPONIBLE'
			  AND (:categoriaId IS NULL OR m.categoria.id = :categoriaId)
			  AND (:razaId IS NULL OR m.raza.id = :razaId)
			  AND (:edadMax IS NULL OR m.edad <= :edadMax)
			  AND (:q IS NULL OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
			       OR LOWER(COALESCE(m.descripcion,'')) LIKE LOWER(CONCAT('%', :q, '%')))
			""")
	List<Mascota> buscarDisponibles(
			@Param("categoriaId") Integer categoriaId,
			@Param("razaId") Integer razaId,
			@Param("edadMax") Integer edadMax,
			@Param("q") String q);

	//Bloqueo pesimista
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM Mascota m WHERE m.id = :id")
	Optional<Mascota> findByIdForUpdate(@Param("id") Integer id);
}
