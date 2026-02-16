package com.lovepaws.app.seguimiento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.lovepaws.app.seguimiento.domain.SeguimientoPostAdopcion;


public interface SeguimientoPostAdopcionRepository extends JpaRepository<SeguimientoPostAdopcion, Integer> {

	 Optional<SeguimientoPostAdopcion> findByIdAndDeletedAtIsNull(Integer id);

	    // Todos los seguimientos activos ordenados por fecha_visita
	    List<SeguimientoPostAdopcion> findAllByOrderByFechaVisitaDesc();

	    // Por estado único
	    List<SeguimientoPostAdopcion> findByEstado_IdOrderByFechaVisitaDesc(String estadoId);

	    // Por lista de estados
	    List<SeguimientoPostAdopcion> findByEstado_IdInOrderByFechaVisitaDesc(List<String> estadoIds);

	    // Filtrado por adoptante
	    List<SeguimientoPostAdopcion> findByAdopcion_UsuarioAdoptante_IdAndDeletedAtIsNullOrderByFechaCreacionDesc(Integer adoptanteId);

	    // Todos activos ordenados por fecha de creación
	    List<SeguimientoPostAdopcion> findByDeletedAtIsNullOrderByFechaCreacionDesc();
}
