package com.lovepaws.app.seguimiento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.seguimiento.domain.SeguimientoPostAdopcion;

public interface SeguimientoPostAdopcionRepository extends JpaRepository<SeguimientoPostAdopcion, Integer> {

    Optional<SeguimientoPostAdopcion> findByIdAndDeletedAtIsNull(Integer id);

    List<SeguimientoPostAdopcion> findByAdopcion_UsuarioAdoptante_IdAndDeletedAtIsNullOrderByFechaCreacionDesc(Integer adoptanteId);

    List<SeguimientoPostAdopcion> findByDeletedAtIsNullOrderByFechaCreacionDesc();
}
