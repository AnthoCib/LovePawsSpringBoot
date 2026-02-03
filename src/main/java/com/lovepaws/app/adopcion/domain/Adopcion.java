package com.lovepaws.app.adopcion.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.lovepaws.app.mascota.domain.Mascota;
import com.lovepaws.app.user.domain.Usuario;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "adopcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE adopcion SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Adopcion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_adoptante")
    private Usuario usuarioAdoptante;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

	@CreationTimestamp
	@Column(name = "fecha_adopcion")
	private LocalDateTime fechaAdopcion;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoAdopcion estado;

	@Column(columnDefinition = "TEXT")
	private String observaciones;

	private Boolean activo;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creacion")
    private Usuario usuarioCreacion;

	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id")
    private SolicitudAdopcion solicitud;

	@CreationTimestamp
	@Column(name = "fecha_creacion", updatable = false)
	private LocalDateTime fechaCreacion;

	@UpdateTimestamp
	@Column(name = "fecha_modificacion")
	private LocalDateTime fechaModificacion;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
