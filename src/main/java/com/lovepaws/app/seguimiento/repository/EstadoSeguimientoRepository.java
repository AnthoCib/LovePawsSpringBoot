package com.lovepaws.app.seguimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;

public interface EstadoSeguimientoRepository extends JpaRepository<EstadoSeguimiento, String> {

    List<EstadoSeguimiento> findByIdIn(List<String> ids);
}
