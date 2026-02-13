package com.lovepaws.app.mascota.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.mascota.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);
}
