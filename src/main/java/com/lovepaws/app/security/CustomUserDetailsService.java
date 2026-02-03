package com.lovepaws.app.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovepaws.app.user.domain.Usuario;

import com.lovepaws.app.user.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarioRepo;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

		// Buscar usuario por username o correo
		Usuario usuario = usuarioRepo.findByUsername(usernameOrEmail)
				.or(() -> usuarioRepo.findByCorreo(usernameOrEmail))
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail));

		// Obtener rol del usuario para authorities
		String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : "ADOPTANTE";

		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase());

		// Pasamos autoridades al UsuarioPrincipal
		return new UsuarioPrincipal(usuario, List.of(authority));

		// Build User (Spring's) with authorities and password
		/*
		 * return User.builder() .username(usuario.getUsername())
		 * .password(usuario.getPasswordHash())
		 * .authorities(Collections.singletonList(authority))
		 * .accountLocked("BLOQUEADO".equalsIgnoreCase(usuario.getEstadoId()))
		 * .disabled(!"ACTIVO".equalsIgnoreCase(usuario.getEstadoId())) .build();
		 */

	}

}
