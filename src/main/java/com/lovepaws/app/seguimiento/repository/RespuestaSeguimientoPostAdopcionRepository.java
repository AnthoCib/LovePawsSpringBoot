package com.lovepaws.app.seguimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.seguimiento.domain.RespuestaSeguimientoPostAdopcion;

public interface RespuestaSeguimientoPostAdopcionRepository extends JpaRepository<RespuestaSeguimientoPostAdopcion, Integer> {

    List<RespuestaSeguimientoPostAdopcion> findBySeguimiento_IdAndDeletedAtIsNullOrderByFechaCreacionAsc(Integer seguimientoId);

    boolean existsBySeguimiento_IdAndDeletedAtIsNull(Integer seguimientoId);
}
