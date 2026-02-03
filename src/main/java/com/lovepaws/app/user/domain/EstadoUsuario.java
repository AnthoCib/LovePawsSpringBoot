package com.lovepaws.app.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoUsuario {
	@Id
	@Column(length = 40)
	private String id;
	private String descripcion;

}
