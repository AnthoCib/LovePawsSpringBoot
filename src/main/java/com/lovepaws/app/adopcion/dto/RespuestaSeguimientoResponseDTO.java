package com.lovepaws.app.adopcion.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespuestaSeguimientoResponseDTO {

    private Integer id;
    private Integer seguimientoId;
    private Integer adopcionId;
    private LocalDateTime fechaRespuesta;
    private String estadoSalud;
    private String comportamiento;
    private String alimentacion;
    private String comentarios;
    private String fotoUrl;
    private Boolean revisado;
    private Integer usuarioCreacionId;
}
