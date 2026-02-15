package com.lovepaws.app.adopcion.repository;

import org.springframework.data.repository.NoRepositoryBean;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;

/**
 * Repositorio legado mantenido solo para compatibilidad de código antiguo.
 *
 * <p>Se marca con {@link NoRepositoryBean} para evitar colisión de bean name
 * con com.lovepaws.app.seguimiento.repository.SeguimientoPostAdopcionRepository.
 * El repositorio activo para adopción es {@link SeguimientoAdopcionRepository}.</p>
 */
@NoRepositoryBean
public interface SeguimientoPostAdopcionRepository extends SeguimientoAdopcionRepository {
    // Sin métodos adicionales: usar SeguimientoAdopcionRepository.
}
