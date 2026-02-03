package com.lovepaws.app.user.service;

import java.util.List;

import com.lovepaws.app.user.domain.Rol;

public interface RolService {
	
	Rol createRol(Rol rol);

	List<Rol> listarRoles();

	Rol updateRol(Rol rol);
}
