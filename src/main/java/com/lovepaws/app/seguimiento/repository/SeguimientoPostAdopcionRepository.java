	package com.lovepaws.app.seguimiento.repository;
	
	import java.util.List;
	import java.util.Optional;
	
	import org.springframework.data.jpa.repository.JpaRepository;
	
	
	import com.lovepaws.app.seguimiento.domain.SeguimientoPostAdopcion;
	
	
	public interface SeguimientoPostAdopcionRepository extends JpaRepository<SeguimientoPostAdopcion, Integer> {
	
		 // Buscar por ID activo
	    Optional<SeguimientoPostAdopcion> findByIdAndDeletedAtIsNull(Integer id);

	    // Todos los seguimientos activos ordenados por fecha de creación
	    List<SeguimientoPostAdopcion> findAllByOrderByFechaCreacionDesc();

	    // Por estado único
	    List<SeguimientoPostAdopcion> findByEstado_IdOrderByFechaCreacionDesc(String estadoId);

	    // Por lista de estados
	    List<SeguimientoPostAdopcion> findByEstado_IdInOrderByFechaCreacionDesc(List<String> estadoIds);

	    // Filtrado por adoptante
	    List<SeguimientoPostAdopcion> findByAdopcion_UsuarioAdoptante_IdAndDeletedAtIsNullOrderByFechaCreacionDesc(Integer adoptanteId);

}

