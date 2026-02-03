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
	
	List<Mascota> findByEstado_Id(String estadoId);

	//Bloqueo pesimista
	@Lock(LockModeType.PESSIMISTIC_WRITE) //Nadie más podrá leer ni escribir ese registro mientras una transacción lo tiene bloqueado
	@Query("SELECT m FROM Mascota m WHERE m.id = :id")
	
	Optional<Mascota> findByIdForUpdate(@Param("id") Integer id);
}
