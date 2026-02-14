package com.lovepaws.app.mascota.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.mascota.domain.Especie;
import com.lovepaws.app.mascota.domain.Raza;

public interface RazaRepository extends JpaRepository<Raza, Integer> {

	List<Raza> findByEspecieId(Integer especieId);


	boolean existsByEspecieIdAndNombreIgnoreCase(Integer especieId, String nombre);

	List<Raza> findByDeletedAtIsNull();

	List<Raza> findByEspecieIdAndDeletedAtIsNull(Integer especieId);

	boolean existsByNombreIgnoreCaseAndEspecieAndDeletedAtIsNull(String nombre, Especie especie);

	boolean existsByNombreIgnoreCaseAndEspecieAndDeletedAtIsNullAndIdNot(String nombre, Especie especie, Integer id);

	long countByEspecieIdAndDeletedAtIsNullAndEstadoTrue(Integer especieId);

}
