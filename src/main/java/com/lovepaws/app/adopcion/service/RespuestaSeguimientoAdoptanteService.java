package com.lovepaws.app.adopcion.service;

import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoRequestDTO;
import com.lovepaws.app.adopcion.dto.RespuestaSeguimientoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface RespuestaSeguimientoAdoptanteService {

    RespuestaSeguimientoResponseDTO registrarRespuesta(RespuestaSeguimientoRequestDTO request,
                                                       MultipartFile foto,
                                                       Integer usuarioId,
                                                       String usuarioNombre);

    RespuestaSeguimientoResponseDTO marcarRevisado(Integer respuestaId,
                                                   boolean revisado,
                                                   Integer usuarioId,
                                                   String usuarioNombre);
}
