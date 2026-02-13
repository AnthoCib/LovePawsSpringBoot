package com.lovepaws.app.adopcion.repository;

import com.lovepaws.app.adopcion.domain.RespuestaSeguimientoAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaSeguimientoAdopcionRepository extends JpaRepository<RespuestaSeguimientoAdopcion, Integer> {
    List<RespuestaSeguimientoAdopcion> findBySeguimiento_Id(Integer seguimientoId);
    List<RespuestaSeguimientoAdopcion> findByAdopcion_Id(Integer adopcionId);
}
