package com.lovepaws.app.mascota.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "especie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE especie SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Especie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
	@Column(name = "nombre", length = 50, nullable = false)
	private String nombre;

	private Boolean estado;

	@Column(name = "id_usuario_creacion")
	private Integer idUsuarioCreacion;

	@CreationTimestamp
	@Column(name = "fecha_creacion", updatable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
