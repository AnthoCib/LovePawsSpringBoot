package com.lovepaws.app.api.dto;

import com.lovepaws.app.mascota.domain.Mascota;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MascotaRequestDTO {

    @NotBlank
    private String nombre;

    @NotNull
    private Integer razaId;

    @NotNull
    private Integer categoriaId;

    @NotNull
    @Min(0)
    private Integer edad;

    @NotNull
    private Mascota.Sexo sexo;

    @Size(max = 255)
    private String descripcion;

    private String fotoUrl;

    private String estadoId;
}
