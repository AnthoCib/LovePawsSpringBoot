package com.lovepaws.app.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE usuario SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "El nombre es obligatorio")
	@Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{2,80}$", message = "El nombre solo debe contener letras y espacios")
	private String nombre;

	@Column(unique = true, nullable = false)
	@Email(message = "El correo debe ser válido")
	@NotBlank(message = "El correo es obligatorio")
	private String correo;

	@Column(unique = true, nullable = false)
	@NotBlank(message = "El username es obligatorio")
	@Pattern(regexp = "^[A-Za-z0-9._-]{4,30}$", message = "El username debe tener entre 4 y 30 caracteres válidos")
	private String username;


	@Column(name = "password_hash", nullable = false)
	@NotBlank(message = "La contraseña es obligatoria")
	private String passwordHash;

	@NotBlank(message = "El teléfono es obligatorio")
	@Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "El teléfono debe contener entre 9 y 15 dígitos")
	private String telefono;

  
    /*@Transient
    private Integer rolId;*/
	@Column(name = "foto_url")
	private String fotoUrl;

	@NotBlank(message = "La direccion es obligatoria")
	@Pattern(regexp = "^.{5,120}$", message = "La dirección debe tener entre 5 y 120 caracteres")
	private String direccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rol_id")
	private Rol rol;

	@Transient
	private Integer rolId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_id")
	private EstadoUsuario estado;

	@Column(name = "id_usuario_creacion")
	private Integer idUsuarioCreacion;

	@CreationTimestamp
	@Column(name = "fecha_creacion", updatable = false)
	private LocalDateTime fechaCreacion;

	@UpdateTimestamp
	@Column(name = "fecha_modificacion")
	private LocalDateTime fechaModificacion;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Transient
	private String resetToken;

	@Transient
	private LocalDateTime resetTokenExpira;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (rol == null)
			return List.of();
		return List.of(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}
}
