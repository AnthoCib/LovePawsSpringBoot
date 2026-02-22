package com.lovepaws.app.seguimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.seguimiento.domain.ResultadoSeguimiento;

public interface ResultadoSeguimientoRepository extends JpaRepository<ResultadoSeguimiento, String> {
    List<ResultadoSeguimiento> findByActivoTrueOrderByDescripcionAsc();
}