package com.lovepaws.app.user.repository;

import com.lovepaws.app.user.domain.Rol;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
	
	Optional<Rol> findByNombre(String nombre);
}
