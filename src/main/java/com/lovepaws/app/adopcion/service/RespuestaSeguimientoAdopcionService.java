package com.lovepaws.app.adopcion.service;

import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoRequestDTO;
import java.util.List;

import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoResponseDTO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface RespuestaSeguimientoAdopcionService {

    List<RespuestaSeguimientoResponseDTO> listarPorAdopcion(Integer adopcionId);

    RespuestaSeguimientoResponseDTO registrarRespuesta(RespuestaSeguimientoRequestDTO request,
                                                       MultipartFile foto,
                                                       Integer usuarioId,
                                                       String usuarioNombre);

    RespuestaSeguimientoResponseDTO marcarRevisado(Integer respuestaId,
                                                   boolean revisado,
                                                   Integer usuarioId,
                                                   String usuarioNombre);
}
