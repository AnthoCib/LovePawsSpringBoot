package com.lovepaws.app.seguimiento.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resultado_seguimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoSeguimiento {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "descripcion", length = 100)
    private String descripcion;
    
    private Boolean activo = true;
}
