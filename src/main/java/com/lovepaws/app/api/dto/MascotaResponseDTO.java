package com.lovepaws.app.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MascotaResponseDTO {
    Integer id;
    String nombre;
    Integer edad;
    String sexo;
    String descripcion;
    String fotoUrl;

    Integer categoriaId;
    String categoriaNombre;

    Integer razaId;
    String razaNombre;

    String estadoId;
    String estadoDescripcion;

    Integer usuarioCreacionId;
}
