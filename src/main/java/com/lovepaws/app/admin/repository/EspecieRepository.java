package com.lovepaws.app.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.admin.domain.Especie;

public interface EspecieRepository extends JpaRepository<Especie, Integer> {

    List<Especie> findByDeletedAtIsNull();

    boolean existsByNombreIgnoreCaseAndDeletedAtIsNull(String nombre);

    boolean existsByNombreIgnoreCaseAndDeletedAtIsNullAndIdNot(String nombre, Integer id);
}
