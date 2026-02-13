package com.lovepaws.app.seguimiento.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_seguimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoSeguimiento {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    public static final String ABIERTO = "ABIERTO";
    public static final String RESPONDIDO = "RESPONDIDO";
    public static final String CERRADO = "CERRADO";
    public static final String ESCALADO = "ESCALADO";
}
