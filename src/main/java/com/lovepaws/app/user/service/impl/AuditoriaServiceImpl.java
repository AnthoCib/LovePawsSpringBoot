package com.lovepaws.app.user.service.impl;

import org.springframework.stereotype.Service;

import com.lovepaws.app.user.domain.Auditoria;
import com.lovepaws.app.user.repository.AuditoriaRepository;
import com.lovepaws.app.user.service.AuditoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

	private final AuditoriaRepository auditoriaRepo;
	
	@Override
	public Auditoria registrar(String tabla, Integer idRegistro, String operacion, Integer idUsuario,
			String usuarioNombre, String detalle) {
		// TODO Auto-generated method stub
		Auditoria a = Auditoria.builder()
                .tabla(tabla)
                .idRegistro(idRegistro)
                .operacion(operacion)
                .idUsuario(idUsuario)
                .usuarioNombre(usuarioNombre)
                .detalle(detalle)
                .build();	
        return auditoriaRepo.save(a);
	}

}
