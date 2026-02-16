package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.lovepaws.app.seguimiento.domain.SeguimientoPostAdopcion;

/**
 * Repositorio legado mantenido solo para compatibilidad de código antiguo.
 *
 * <p>Se marca con {@link NoRepositoryBean} para evitar colisión de bean name
 * con com.lovepaws.app.seguimiento.repository.SeguimientoPostAdopcionRepository.
 * El repositorio activo para adopción es {@link SeguimientoAdopcionRepository}.</p>
 */
@NoRepositoryBean
public interface SeguimientoPostAdopcionRepository extends JpaRepository<SeguimientoPostAdopcion, Integer> {
    // Sin métodos adicionales: usar SeguimientoAdopcionRepository.
	
	List<SeguimientoPostAdopcion> findByEstado_IdOrderByFechaVisitaDesc(String estadoId);

	List<SeguimientoPostAdopcion> findAllByOrderByFechaVisitaDesc();
	
	List<SeguimientoPostAdopcion> findByEstado_IdInOrderByFechaVisitaDesc(List<String> ids);
}
