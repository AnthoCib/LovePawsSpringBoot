package com.lovepaws.app.adopcion.repository;

import com.lovepaws.app.adopcion.domain.RespuestaSeguimientoAdoptante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RespuestaSeguimientoAdoptanteRepository extends JpaRepository<RespuestaSeguimientoAdoptante, Integer> {
    List<RespuestaSeguimientoAdoptante> findBySeguimiento_Id(Integer seguimientoId);
    List<RespuestaSeguimientoAdoptante> findByAdopcion_Id(Integer adopcionId);
    Optional<RespuestaSeguimientoAdoptante> findTopBySeguimiento_IdAndUsuarioCreacion_IdOrderByFechaRespuestaDesc(
            Integer seguimientoId,
            Integer usuarioCreacionId);
}
