package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lovepaws.app.seguimiento.domain.SeguimientoPostAdopcion;



public interface SeguimientoPostAdopcionRepository extends JpaRepository<SeguimientoPostAdopcion, Integer> {
 
	
	List<SeguimientoPostAdopcion> findByEstado_IdOrderByFechaVisitaDesc(String estadoId);

	List<SeguimientoPostAdopcion> findAllByOrderByFechaVisitaDesc();
	
	List<SeguimientoPostAdopcion> findByEstado_IdInOrderByFechaVisitaDesc(List<String> ids);
}
