package com.lovepaws.app.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.admin.domain.Especie;
import com.lovepaws.app.admin.domain.Raza;

public interface RazaRepository extends JpaRepository<Raza, Integer> {

    List<Raza> findByDeletedAtIsNull();

    List<Raza> findByEspecieIdAndDeletedAtIsNull(Integer especieId);

    boolean existsByNombreIgnoreCaseAndEspecieAndDeletedAtIsNull(String nombre, Especie especie);

    boolean existsByNombreIgnoreCaseAndEspecieAndDeletedAtIsNullAndIdNot(String nombre, Especie especie, Integer id);

    long countByEspecieIdAndDeletedAtIsNullAndEstadoTrue(Integer especieId);
}
