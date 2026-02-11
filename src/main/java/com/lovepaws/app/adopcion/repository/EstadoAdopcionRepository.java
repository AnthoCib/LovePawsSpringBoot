package com.lovepaws.app.adopcion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.adopcion.domain.EstadoAdopcion;

public interface EstadoAdopcionRepository extends JpaRepository<EstadoAdopcion, String> {
}
