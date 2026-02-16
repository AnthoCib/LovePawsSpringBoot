package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;


public interface SeguimientoPostAdopcionTrackingRepository extends JpaRepository<SeguimientoAdopcion, Integer> {

    List<SeguimientoAdopcion> findAllByOrderByFechaCreacionDesc();
    List<SeguimientoAdopcion> findByEstado_IdOrderByFechaCreacionDesc(String estadoId);

    List<SeguimientoAdopcion> findByEstado_IdInOrderByFechaCreacionDesc(List<String> ids);
}
