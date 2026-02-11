package com.lovepaws.app.adopcion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarEstadoSolicitudDTO {

    // Estado solicitado desde frontend (ENVIADA, EN_REVISION, APROBADA, RECHAZADA)
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    // Observación opcional para auditoría o revisión interna
    private String comentario;
}
