package com.lovepaws.app.seguimiento.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.seguimiento.domain.EstadoSeguimiento;

public interface EstadoSeguimientoRepository extends JpaRepository<EstadoSeguimiento, String> {
    Optional<EstadoSeguimiento> findById(String id);

    List<EstadoSeguimiento> findByIdInOrderByDescripcionAsc(Collection<String> ids);
}
