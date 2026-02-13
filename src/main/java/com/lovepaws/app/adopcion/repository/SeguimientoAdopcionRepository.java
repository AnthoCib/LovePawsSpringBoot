package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.adopcion.domain.SeguimientoAdopcion;

public interface SeguimientoAdopcionRepository extends JpaRepository<SeguimientoAdopcion, Integer> {

	List<SeguimientoAdopcion> findByAdopcionIdOrderByFechaVisitaDesc(Integer adopcionId);


	List<SeguimientoAdopcion> findAllByOrderByFechaVisitaDesc();

	List<SeguimientoAdopcion> findByEstado_IdOrderByFechaVisitaDesc(String estadoId);

}
