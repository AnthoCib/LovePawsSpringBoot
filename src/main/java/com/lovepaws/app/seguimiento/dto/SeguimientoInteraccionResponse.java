package com.lovepaws.app.seguimiento.dto;

import java.time.LocalDateTime;

import com.lovepaws.app.seguimiento.domain.TipoInteraccionSeguimiento;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeguimientoInteraccionResponse {
    private Long id;
    private Integer autorId;
    private String autorNombre;
    private TipoInteraccionSeguimiento tipoAutor;
    private String mensaje;
    private LocalDateTime fechaCreacion;
}
