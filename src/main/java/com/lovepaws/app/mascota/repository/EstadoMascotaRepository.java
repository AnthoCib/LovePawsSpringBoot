package com.lovepaws.app.mascota.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.mascota.domain.EstadoMascota;

public interface EstadoMascotaRepository extends JpaRepository<EstadoMascota, String> {

}
