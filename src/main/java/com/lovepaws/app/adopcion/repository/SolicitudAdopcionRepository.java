package com.lovepaws.app.adopcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lovepaws.app.adopcion.domain.SolicitudAdopcion;

import jakarta.persistence.LockModeType;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Integer> {

	List<SolicitudAdopcion> findByMascota_IdAndEstado_Id(Integer mascotaId, String estadoId);

	List<SolicitudAdopcion> findByUsuario_Id(Integer idUsuario);	

	boolean existsByUsuario_IdAndMascota_IdAndEstado_Id(Integer usuarioId, Integer mascotaId, String estadoId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM SolicitudAdopcion s WHERE s.id = :id")
	Optional<SolicitudAdopcion> findByIdForUpdate(@Param("id") Integer id);

	long countByEstadoId(String estadoId);

}
