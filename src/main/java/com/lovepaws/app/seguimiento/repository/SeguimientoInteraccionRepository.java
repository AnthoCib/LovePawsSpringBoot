package com.lovepaws.app.seguimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.seguimiento.domain.SeguimientoInteraccion;

public interface SeguimientoInteraccionRepository extends JpaRepository<SeguimientoInteraccion, Long> {

    List<SeguimientoInteraccion> findBySeguimiento_IdAndDeletedAtIsNullOrderByFechaCreacionAsc(Long seguimientoId);
}
