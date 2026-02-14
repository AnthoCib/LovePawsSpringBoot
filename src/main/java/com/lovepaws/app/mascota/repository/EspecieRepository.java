package com.lovepaws.app.mascota.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.mascota.domain.Especie;

public interface EspecieRepository extends JpaRepository<Especie, Integer>{


    boolean existsByNombreIgnoreCase(String nombre);


    List<Especie> findByDeletedAtIsNull();

    boolean existsByNombreIgnoreCaseAndDeletedAtIsNull(String nombre);

    boolean existsByNombreIgnoreCaseAndDeletedAtIsNullAndIdNot(String nombre, Integer id);

}
