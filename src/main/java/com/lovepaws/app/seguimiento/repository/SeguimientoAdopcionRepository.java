package com.lovepaws.app.seguimiento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.seguimiento.domain.SeguimientoAdopcion;

public interface SeguimientoAdopcionRepository extends JpaRepository<SeguimientoAdopcion, Long> {

    Optional<SeguimientoAdopcion> findByIdAndDeletedAtIsNull(Long id);

    List<SeguimientoAdopcion> findByAdopcion_UsuarioAdoptante_IdAndDeletedAtIsNullOrderByFechaCreacionDesc(Integer adoptanteId);

    List<SeguimientoAdopcion> findByDeletedAtIsNullOrderByFechaCreacionDesc();
}
