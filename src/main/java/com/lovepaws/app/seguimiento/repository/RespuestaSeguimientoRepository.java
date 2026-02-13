package com.lovepaws.app.seguimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.seguimiento.domain.RespuestaSeguimientoAdoptante;

public interface RespuestaSeguimientoRepository extends JpaRepository<RespuestaSeguimientoAdoptante, Integer> {

    List<RespuestaSeguimientoAdoptante> findBySeguimiento_IdAndDeletedAtIsNullOrderByFechaCreacionAsc(Integer seguimientoId);

    boolean existsBySeguimiento_IdAndDeletedAtIsNull(Integer seguimientoId);
}
