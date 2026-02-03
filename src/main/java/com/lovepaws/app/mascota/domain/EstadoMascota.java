package com.lovepaws.app.mascota.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "estado_mascota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoMascota {
    @Id
    @Column(length = 40)
    private String id;

    private String descripcion;

}
