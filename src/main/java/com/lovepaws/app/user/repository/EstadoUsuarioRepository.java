package com.lovepaws.app.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovepaws.app.user.domain.EstadoUsuario;

public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, String>{

}
