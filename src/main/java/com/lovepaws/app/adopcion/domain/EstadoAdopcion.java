package com.lovepaws.app.adopcion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_adopcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoAdopcion {
	@Id
	@Column(length = 40)
	private String id;

	private String descripcion;
}
