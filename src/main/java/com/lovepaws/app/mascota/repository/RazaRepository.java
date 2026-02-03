package com.lovepaws.app.mascota.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.mascota.domain.Raza;

public interface RazaRepository extends JpaRepository<Raza, Integer> {
	
	List<Raza> findByEspecieId(Integer especieId);
}
