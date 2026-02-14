package com.lovepaws.app.mascota.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.mascota.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {


    boolean existsByNombreIgnoreCase(String nombre);

    List<Categoria> findByDeletedAtIsNull();

    boolean existsByNombreIgnoreCaseAndDeletedAtIsNull(String nombre);

    boolean existsByNombreIgnoreCaseAndDeletedAtIsNullAndIdNot(String nombre, Integer id);

}
