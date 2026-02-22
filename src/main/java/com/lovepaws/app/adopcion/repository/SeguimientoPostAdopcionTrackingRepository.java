package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;


public interface SeguimientoPostAdopcionTrackingRepository extends JpaRepository<SeguimientoPostAdopcion, Integer> {

    List<SeguimientoPostAdopcion> findAllByOrderByFechaCreacionDesc();
    
    List<SeguimientoPostAdopcion> findByEstado_IdOrderByFechaCreacionDesc(String estadoId);

    List<SeguimientoPostAdopcion> findByEstado_IdInOrderByFechaCreacionDesc(List<String> ids);
}
