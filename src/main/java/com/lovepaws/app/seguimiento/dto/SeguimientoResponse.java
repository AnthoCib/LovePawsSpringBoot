package com.lovepaws.app.seguimiento.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeguimientoResponse {
    private Integer id;
    private Integer adopcionId;
    private Integer adoptanteId;
    private String mascotaNombre;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private List<RespuestaItem> historial;

    @Data
    @Builder
    public static class RespuestaItem {
        private Integer id;
        private Integer autorId;
        private String autorNombre;
        private String mensaje;
        private Boolean revisado;
        private LocalDateTime fechaCreacion;
    }
}
