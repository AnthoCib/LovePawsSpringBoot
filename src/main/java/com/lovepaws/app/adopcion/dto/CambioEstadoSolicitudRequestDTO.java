package com.lovepaws.app.adopcion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CambioEstadoSolicitudRequestDTO {

    @NotBlank(message = "accion es obligatoria")
    private String accion; // APROBAR o RECHAZAR

    private String motivo;
}
