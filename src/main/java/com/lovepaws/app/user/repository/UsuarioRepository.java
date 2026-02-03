package com.lovepaws.app.user.repository;

import com.lovepaws.app.user.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
	
	Optional<Usuario> findByUsername(String username);

	Optional<Usuario> findByCorreo(String correo);

	boolean existsByUsername(String username);

	boolean existsByCorreo(String correo);
	
	long countByRolNombre(String nombre);
	
	long countByDeletedAtIsNull();


}
