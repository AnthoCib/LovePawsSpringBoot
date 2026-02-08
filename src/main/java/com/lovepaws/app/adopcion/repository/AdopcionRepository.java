package com.lovepaws.app.adopcion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.adopcion.domain.Adopcion;

public interface AdopcionRepository  extends JpaRepository<Adopcion, Integer>{
	
	List<Adopcion> findByUsuarioAdoptante_Id(Integer idUsuarioAdoptante);

	List<Adopcion> findByMascotaId(Integer mascotaId);

	long countByEstado_Id(String estadoId);

}
