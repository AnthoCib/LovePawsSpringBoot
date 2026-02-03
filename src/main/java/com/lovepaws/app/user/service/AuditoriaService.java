package com.lovepaws.app.user.service;

import com.lovepaws.app.user.domain.Auditoria;

public interface AuditoriaService {
	
	Auditoria registrar(String tabla, Integer idRegistro, String operacion, Integer idUsuario, String usuarioNombre,
			String detalle);
}
