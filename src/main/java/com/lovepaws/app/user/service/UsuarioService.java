package com.lovepaws.app.user.service;

import java.util.List;
import java.util.Optional;

import com.lovepaws.app.user.domain.Usuario;

public interface UsuarioService {
	
	void crearUsuarioDesdeAdmin(Usuario usuario);

	Usuario createUsuario(Usuario usuario);

	Usuario updateUsuario(Usuario usuario);

	List<Usuario> listarUsuarios();

	Optional<Usuario> findUsuarioById(Integer idUsuario);

	Optional<Usuario> findByUsername(String username);

	void deleteUsuarioById(Integer idUsuario); // soft delete

	Optional<Usuario> findByCorreo(String correo);

	void cambiarEstado(Integer id);

	void cambiarRol(Integer usuarioId, Integer rolId);
}
