package com.lovepaws.app.user.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lovepaws.app.user.domain.Rol;
import com.lovepaws.app.user.repository.RolRepository;
import com.lovepaws.app.user.service.RolService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService{

	private final RolRepository rolRepo;
	
	@Override
	public Rol createRol(Rol rol) {
		// TODO Auto-generated method stub
		return rolRepo.save(rol);
	}

	@Override
	public List<Rol> listarRoles() {
		// TODO Auto-generated method stub
		return rolRepo.findAll();
	}

	@Override
	public Rol updateRol(Rol rol) {
		// TODO Auto-generated method stub
		return rolRepo.save(rol);
	}

}
