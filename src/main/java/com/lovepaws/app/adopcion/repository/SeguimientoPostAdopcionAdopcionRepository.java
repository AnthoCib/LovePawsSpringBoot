package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lovepaws.app.seguimiento.domain.SeguimientoPostAdopcion;



public interface SeguimientoPostAdopcionAdopcionRepository extends JpaRepository<SeguimientoPostAdopcion, Integer> {
 
	
	List<SeguimientoPostAdopcion> findByEstado_IdOrderByFechaCreacionDesc(String estadoId);

	List<SeguimientoPostAdopcion> findAllByOrderByFechaCreacionDesc();
	
	List<SeguimientoPostAdopcion> findByEstado_IdInOrderByFechaCreacionDesc(List<String> ids);
}
