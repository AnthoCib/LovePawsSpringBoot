package com.lovepaws.app.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.lovepaws.app.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsuarioPrincipal implements UserDetails {


	private static final long serialVersionUID = 1L;

	private final Usuario usuario;
	
	private final Collection<? extends GrantedAuthority> authorities;
	
   
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return usuario.getPasswordHash();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return usuario.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return "ACTIVO".equalsIgnoreCase(usuario.getEstado().getId());
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return "ACTIVO".equalsIgnoreCase(usuario.getEstado().getId());
	}

	// getter for Usuario if needed
	public Usuario getUsuario() {
		return usuario;
	}

}
