package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.adopcion.domain.SeguimientoPostAdopcion;

public interface SeguimientoRepository extends JpaRepository<SeguimientoPostAdopcion, Integer> {

	List<SeguimientoPostAdopcion> findByAdopcionIdOrderByFechaVisitaDesc(Integer adopcionId);


	List<SeguimientoPostAdopcion> findAllByOrderByFechaVisitaDesc();

	List<SeguimientoPostAdopcion> findByEstado_IdOrderByFechaVisitaDesc(String estadoId);

}
