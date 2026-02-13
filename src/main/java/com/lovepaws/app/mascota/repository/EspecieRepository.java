package com.lovepaws.app.mascota.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.mascota.domain.Especie;

public interface EspecieRepository extends JpaRepository<Especie, Integer>{

    boolean existsByNombreIgnoreCase(String nombre);

}
