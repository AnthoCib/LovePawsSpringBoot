package com.lovepaws.app.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class Usuario  implements UserDetails{
	
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(unique = true, nullable = false)
    @Email(message = "El correo debe ser válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String passwordHash;
    
    @NotBlank(message = "El telefono es obligatoria")
    private String telefono;
    
    private String fotoUrl;
    
    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    
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

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		   if (rol == null) return List.of();
		    return List.of(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}
}
